package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

/** Portal -> opted-in personal calendars. Unrelated provider events are never imported, edited or deleted. */
@Service
public class CalendarSyncService {
 private final CalendarConnectionRepository connections;private final CalendarEventLinkRepository links;private final PortalMeetingRepository meetings;
 private final CalendarConnectionService credentials;private final CalendarSettings settings;private final CalendarHttpClient http;private final PortalActor actor;
 public CalendarSyncService(CalendarConnectionRepository connections,CalendarEventLinkRepository links,PortalMeetingRepository meetings,CalendarConnectionService credentials,CalendarSettings settings,CalendarHttpClient http,PortalActor actor){this.connections=connections;this.links=links;this.meetings=meetings;this.credentials=credentials;this.settings=settings;this.http=http;this.actor=actor;}
 public record LinkStatus(Long id,Long meetingId,String provider,String status,String lastError,String syncedAt){}
 private LinkStatus view(CalendarEventLink l){return new LinkStatus(l.id,l.meeting.id,l.connection.provider,l.status,l.lastError,l.syncedAt==null?null:l.syncedAt.toString());}
 @Transactional(readOnly=true) public List<LinkStatus> list(){return links.findByConnectionOwnerId(actor.id()).stream().map(this::view).toList();}
 @Transactional public LinkStatus select(String provider,Long meetingId){
  String p=settings.name(provider);settings.provider(p);CalendarConnection c=connections.lockOwned(actor.id(),p).orElseThrow(()->new ResponseStatusException(HttpStatus.CONFLICT,"Connect your calendar first"));
  if(!"CONNECTED".equals(c.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"Reconnect your calendar");
  PortalMeeting m=meetings.findById(meetingId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  if("CANCELLED".equals(m.status)||m.endsAt.isBefore(OffsetDateTime.now()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Select an upcoming meeting");
  CalendarEventLink l=links.findByConnectionIdAndMeetingId(c.id,m.id).orElseGet(CalendarEventLink::new);l.connection=c;l.meeting=m;
  if("UNSUBSCRIBED".equals(l.status))l.remoteId=null;
  l.status="PENDING";l.attempts=0;l.nextAttemptAt=OffsetDateTime.now();l.lastError=null;return view(links.save(l));
 }
 /** Explicitly stop syncing this selected meeting and remove only its managed external copy. */
 @Transactional public void unselect(Long id){
  CalendarEventLink current=links.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  if(!current.connection.ownerId.equals(actor.id()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  connections.lock(current.connection.id).orElseThrow();CalendarEventLink l=links.lock(id).orElseThrow();
  if("UNSUBSCRIBED".equals(l.status))return;
  l.status="UNSUBSCRIBE_PENDING";l.attempts=0;l.nextAttemptAt=OffsetDateTime.now();l.lastError=null;
 }
 @Transactional public void retry(Long id){
  CalendarEventLink initial=links.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  if(!initial.connection.ownerId.equals(actor.id()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  connections.lock(initial.connection.id).orElseThrow();CalendarEventLink l=links.lock(id).orElseThrow();
  if(!Set.of("FAILED","RETRY","UNSUBSCRIBE_FAILED","UNSUBSCRIBE_RETRY").contains(l.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"Only failed calendar syncs can be retried");
  l.status=l.status.startsWith("UNSUBSCRIBE_")?"UNSUBSCRIBE_PENDING":"PENDING";l.attempts=0;l.nextAttemptAt=OffsetDateTime.now();
 }
 @Transactional public void sync(Long id){
  CalendarEventLink initial=links.findById(id).orElse(null);if(initial==null)return;
  CalendarConnection c=connections.lock(initial.connection.id).orElse(null);if(c==null||!"CONNECTED".equals(c.status))return;
  CalendarEventLink l=links.lock(id).orElse(null);if(l==null)return;
  PortalMeeting m=l.meeting;
  boolean unselect=l.status.startsWith("UNSUBSCRIBE_");
  if(l.attemptedSequence==m.calendarSequence&&!unselect&&!Set.of("PENDING","RETRY").contains(l.status))return;
  if(l.attemptedSequence==m.calendarSequence&&l.nextAttemptAt!=null&&l.nextAttemptAt.isAfter(OffsetDateTime.now()))return;
  if(l.attemptedSequence!=m.calendarSequence&&!unselect)l.attempts=0;
  l.attemptedSequence=m.calendarSequence;l.attempts++;
  try{
   String access=credentials.accessTokenLocked(c);
   if(c.provider.equals("google"))google(l,access,unselect);else microsoft(l,access,unselect);
   l.lastSequence=m.calendarSequence;l.status=unselect?"UNSUBSCRIBED":"CANCELLED".equals(m.status)?"DELETED":"SYNCED";l.lastError=null;l.syncedAt=OffsetDateTime.now();c.lastSyncedAt=l.syncedAt;c.lastError=null;
  }catch(CalendarHttpClient.RemoteFailure ex){
   if(ex.status==401){c.accessExpiresAt=OffsetDateTime.now().minusSeconds(1);}
   l.status=unselect?(ex.status==403||l.attempts>=5?"UNSUBSCRIBE_FAILED":"UNSUBSCRIBE_RETRY"):(ex.status==403||l.attempts>=5?"FAILED":"RETRY");l.nextAttemptAt=OffsetDateTime.now().plusMinutes(Math.min(60,1L<<Math.min(l.attempts,6)));
   l.lastError=ex.status==401?"Reconnect or wait for token refresh":ex.status==403?"Calendar write permission denied":"Provider unavailable or rate-limited; retry is tracked";
   c.lastError=l.lastError;
  }catch(ResponseStatusException|IllegalStateException ex){l.status=unselect?"UNSUBSCRIBE_FAILED":"FAILED";l.lastError="Calendar configuration or encryption key requires administrator review";c.lastError=l.lastError;}
 }
 private String stableId(CalendarEventLink l){
  // Google Calendar accepts custom event IDs containing base32hex characters (0-9, a-v).
  // A SHA-256 lowercase hexadecimal subset (0-9, a-f) is valid on both providers.
  return "bt"+IntegrationCrypto.sha256(l.connection.ownerId+":"+l.meeting.id);
 }
 private void good(CalendarHttpClient.Result r){if(r.status()<200||r.status()>=300)throw new CalendarHttpClient.RemoteFailure(r.status());}
 private void google(CalendarEventLink l,String access,boolean unselect){
  String root="https://www.googleapis.com/calendar/v3/calendars/primary/events",id=l.remoteId==null?stableId(l):l.remoteId;
  String url=root+"/"+CalendarHttpClient.encode(id);PortalMeeting m=l.meeting;
  if(unselect||"CANCELLED".equals(m.status)){
   CalendarHttpClient.Result r=http.api(url,"DELETE",access,null);if(r.status()!=404&&r.status()!=410)good(r);return;
  }
  Map<String,Object> body=new LinkedHashMap<>();body.put("summary",m.title);body.put("description",(m.agenda==null?"":m.agenda)+"\n"+m.meetingUrl);body.put("location",m.meetingUrl);
  body.put("start",Map.of("dateTime",m.startsAt.toString()));body.put("end",Map.of("dateTime",m.endsAt.toString()));
  body.put("extendedProperties",Map.of("private",Map.of("bluethinkMeetingId",String.valueOf(m.id))));
  CalendarHttpClient.Result response=http.api(url,"PATCH",access,body);
  if(response.status()==404){body.put("id",id);response=http.api(root,"POST",access,body);if(response.status()==409){body.remove("id");response=http.api(url,"PATCH",access,body);}}
  good(response);l.remoteId=id;
 }
 private void microsoft(CalendarEventLink l,String access,boolean unselect){
  String root="https://graph.microsoft.com/v1.0/me/events";PortalMeeting m=l.meeting;
  String propertyId="String {4cb593b1-ec25-4b7f-a752-6b53c0110069} Name BlueThinkPortalEvent";
  if(l.remoteId==null){
   String filter="singleValueExtendedProperties/Any(ep: ep/id eq '"+propertyId+"' and ep/value eq '"+stableId(l)+"')";
   CalendarHttpClient.Result lookup=http.api(root+"?$select=id&$top=2&$filter="+CalendarHttpClient.encode(filter),"GET",access,null);good(lookup);
   Object value=lookup.body().get("value");
   if(!(value instanceof List<?> values))throw new CalendarHttpClient.RemoteFailure(502);
   if(values.size()>1)throw new IllegalStateException("Duplicate remote portal copies need administrator review");
   if(!values.isEmpty()&&values.get(0) instanceof Map<?,?> event&&event.get("id") instanceof String found)l.remoteId=found;
  }
  if(unselect||"CANCELLED".equals(m.status)){
   if(l.remoteId!=null){CalendarHttpClient.Result r=http.api(root+"/"+CalendarHttpClient.encode(l.remoteId),"DELETE",access,null);if(r.status()!=404&&r.status()!=410)good(r);}return;
  }
  Map<String,Object> body=new LinkedHashMap<>();body.put("subject",m.title);body.put("body",Map.of("contentType","text","content",(m.agenda==null?"":m.agenda)+"\n"+m.meetingUrl));
  body.put("start",Map.of("dateTime",m.startsAt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime().toString(),"timeZone","UTC"));
  body.put("end",Map.of("dateTime",m.endsAt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime().toString(),"timeZone","UTC"));body.put("location",Map.of("displayName",m.meetingUrl));
  CalendarHttpClient.Result r;
  if(l.remoteId==null){body.put("singleValueExtendedProperties",List.of(Map.of("id",propertyId,"value",stableId(l))));body.put("transactionId",UUID.nameUUIDFromBytes(stableId(l).getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString());r=http.api(root,"POST",access,body);good(r);Object remote=r.body().get("id");if(!(remote instanceof String s)||s.isBlank()||s.length()>1000)throw new CalendarHttpClient.RemoteFailure(502);l.remoteId=s;}
  else{r=http.api(root+"/"+CalendarHttpClient.encode(l.remoteId),"PATCH",access,body);good(r);}
 }
}
