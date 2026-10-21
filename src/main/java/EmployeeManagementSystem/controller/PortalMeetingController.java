package EmployeeManagementSystem.controller;
import EmployeeManagementSystem.entity.PortalMeeting;
import EmployeeManagementSystem.repository.PortalMeetingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.*;
import java.time.format.DateTimeFormatter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.util.StringUtils;
import java.time.OffsetDateTime;
import java.util.*;
@RestController @RequestMapping("/api/meetings")
public class PortalMeetingController {
 private final PortalMeetingRepository repository;
 private final EmployeeManagementSystem.service.PortalActor actor;
 private final EmployeeManagementSystem.service.MeetingReminderService reminderService;
 private final ObjectProvider<JavaMailSender> mailProvider;
 @Value("${bluethink.meetings.email.enabled:false}") private boolean mailEnabled;
 @Value("${bluethink.meetings.email.from:}") private String fromAddress;
 public PortalMeetingController(PortalMeetingRepository repository,ObjectProvider<JavaMailSender> mailProvider,EmployeeManagementSystem.service.MeetingReminderService reminderService,EmployeeManagementSystem.service.PortalActor actor){this.actor=actor;this.repository=repository;this.mailProvider=mailProvider;this.reminderService=reminderService;}
 private Authentication auth(){Authentication a=SecurityContextHolder.getContext().getAuthentication();if(a==null||!a.isAuthenticated()||a instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);return a;}
 private boolean editor(){return auth().getAuthorities().stream().anyMatch(a->Set.of("ROLE_ADMIN","ROLE_HR","ROLE_MANAGER").contains(a.getAuthority()));}
 private void requireEditor(){if(!editor())throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Manager, HR or Admin permission required");}
 public record Request(String title,OffsetDateTime startsAt,OffsetDateTime endsAt,String meetingUrl,String agenda,String timeZone){}
 public record RecurrenceRequest(Request meeting,String frequency,int occurrences){}
 private String clean(String s,int max,String field){String v=s==null?"":s.trim();if(v.isBlank()||v.length()>max)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid "+field);return v;}
 private String safeUrl(String input){return EmployeeManagementSystem.service.MeetingTimeRules.safeUrl(input);}
 private void fill(PortalMeeting meeting,Request r){if(r==null||r.startsAt()==null||r.endsAt()==null||!r.endsAt().isAfter(r.startsAt())||r.endsAt().isAfter(r.startsAt().plusDays(2)))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Meeting must have a valid start and end (up to two days)");meeting.timeZone=EmployeeManagementSystem.service.MeetingTimeRules.zone(r.timeZone()).getId();meeting.title=clean(r.title(),160,"title");meeting.startsAt=r.startsAt();meeting.endsAt=r.endsAt();meeting.meetingUrl=safeUrl(r.meetingUrl());meeting.agenda=r.agenda()==null?"":r.agenda().trim();if(meeting.agenda.length()>1200)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Agenda is too long");}
 @GetMapping @Transactional(readOnly=true) public List<PortalMeeting> list(@RequestParam String month){auth();if(!month.matches("\\d{4}-(0[1-9]|1[0-2])"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Month must be YYYY-MM");java.time.YearMonth ym=java.time.YearMonth.parse(month);OffsetDateTime from=ym.atDay(1).atStartOfDay().atOffset(java.time.ZoneOffset.UTC);return repository.findByStartsAtGreaterThanEqualAndStartsAtLessThanOrderByStartsAtAsc(from.minusDays(1),from.plusMonths(1).plusDays(1));}
 @GetMapping(value="/{id}/invite.ics",produces="text/calendar") @Transactional(readOnly=true)
 public ResponseEntity<String> calendarInvite(@PathVariable Long id){auth();PortalMeeting m=repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  String invite=EmployeeManagementSystem.service.MeetingCalendarText.build(m.id,m.title,m.agenda,m.meetingUrl,m.startsAt,m.endsAt,m.calendarSequence,"CANCELLED".equals(m.status),null,null);
  return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=bluethink-meeting-"+id+".ics").header(HttpHeaders.CACHE_CONTROL,"private, no-store").header("X-Content-Type-Options","nosniff").body(invite);
 }
 private String ics(String s){return (s==null?"":s).replace("\\","\\\\").replace(";","\\;").replace(",","\\,").replace("\r","").replace("\n","\\n");}

 @PostMapping("/{id}/reminders") public List<EmployeeManagementSystem.service.MeetingReminderService.Summary> scheduleReminders(@PathVariable Long id,@RequestBody EmployeeManagementSystem.service.MeetingReminderService.Request input){requireEditor();return reminderService.schedule(id,input);}
 @GetMapping("/{id}/reminders") public List<EmployeeManagementSystem.service.MeetingReminderService.Summary> listReminders(@PathVariable Long id){requireEditor();return reminderService.list(id);}
 public record InvitationRequest(List<String> recipients){}
 @PostMapping("/{id}/send-invitations")
 public Map<String,Object> sendInvitations(@PathVariable Long id,@RequestBody InvitationRequest request){
  requireEditor();return reminderService.invite(id,request==null?null:request.recipients());
 }
 public record RetryRequest(String reason,boolean acceptDuplicateRisk){}
 @PostMapping("/deliveries/{deliveryId}/retry")
 public EmployeeManagementSystem.service.MeetingReminderService.Summary retry(@PathVariable Long deliveryId,@RequestBody RetryRequest input){
  requireEditor();return reminderService.retry(deliveryId,input.reason(),input.acceptDuplicateRisk());
 }
 /** Atomically create at most 52 linked occurrences; no silent employee invitations. */
 @PostMapping("/recurring") @Transactional
 public List<PortalMeeting> recurring(@RequestBody RecurrenceRequest input){
  requireEditor();
  if(input==null||input.meeting()==null||input.occurrences()<2||input.occurrences()>52)
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Provide 2 to 52 occurrences");
  String frequency=input.frequency()==null?"":input.frequency().toUpperCase(Locale.ROOT);
  if(!Set.of("DAILY","WEEKLY","MONTHLY").contains(frequency))
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Frequency must be DAILY, WEEKLY or MONTHLY");
  PortalMeeting template=new PortalMeeting();fill(template,input.meeting());
  if(template.startsAt.isBefore(OffsetDateTime.now().minusMinutes(5)))
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Recurring meetings must start in the future");
  String seriesId=UUID.randomUUID().toString();List<PortalMeeting> meetings=new ArrayList<>();
  for(int i=0;i<input.occurrences();i++){
   PortalMeeting m=new PortalMeeting();fill(m,input.meeting());
   var occurrence=EmployeeManagementSystem.service.MeetingTimeRules.occurrence(template.startsAt,template.endsAt,frequency,i,template.timeZone);
   m.startsAt=occurrence.startsAt();m.endsAt=occurrence.endsAt();
   m.organizer=auth().getName();m.seriesId=seriesId;meetings.add(m);
  }
  return repository.saveAll(meetings);
 }
 @GetMapping("/series/{seriesId}") @Transactional(readOnly=true)
 public List<PortalMeeting> series(@PathVariable String seriesId){auth();
  try{UUID.fromString(seriesId);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid series ID");}
  return repository.findBySeriesIdOrderByStartsAtAsc(seriesId);
 }
 @DeleteMapping("/series/{seriesId}") @Transactional
 public Map<String,Object> cancelSeries(@PathVariable String seriesId){requireEditor();
  try{UUID.fromString(seriesId);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid series ID");}
  List<PortalMeeting> items=repository.findBySeriesIdOrderByStartsAtAsc(seriesId);
  for(PortalMeeting item:items){repository.lock(item.id).orElseThrow();actor.edit(item);}
  int count=0;for(PortalMeeting m:items)if(!"CANCELLED".equals(m.status)&&m.startsAt.isAfter(OffsetDateTime.now())){m.status="CANCELLED";m.calendarSequence++;count++;}
  repository.saveAll(items);for(PortalMeeting item:items)if("CANCELLED".equals(item.status))reminderService.meetingChanged(item);return Map.of("cancelled",count,"seriesId",seriesId);
 }
 @PostMapping @Transactional public PortalMeeting create(@RequestBody Request req){requireEditor();PortalMeeting m=new PortalMeeting();fill(m,req);if(!m.startsAt.isAfter(OffsetDateTime.now()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Schedule a future start time");m.organizer=auth().getName();return repository.save(m);}
 @PutMapping("/{id}") @Transactional public PortalMeeting update(@PathVariable Long id,@RequestBody Request req){requireEditor();PortalMeeting m=repository.lock(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));actor.edit(m);if("CANCELLED".equals(m.status)||!m.startsAt.isAfter(OffsetDateTime.now()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Only upcoming meetings can be edited");fill(m,req);if(!m.startsAt.isAfter(OffsetDateTime.now()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Schedule a future start time");m.calendarSequence++;reminderService.meetingChanged(m);return repository.save(m);}
 @DeleteMapping("/{id}") @Transactional public Map<String,Object> cancel(@PathVariable Long id){requireEditor();PortalMeeting m=repository.lock(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));actor.edit(m);if("CANCELLED".equals(m.status))return Map.of("id",id,"status",m.status);m.status="CANCELLED";m.calendarSequence++;repository.save(m);reminderService.meetingChanged(m);return Map.of("id",id,"status",m.status);}
}
