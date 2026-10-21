package EmployeeManagementSystem.service;
import java.time.*;
import java.net.URI;
import java.util.*;
/** Keeps recurring meetings at the organizer's local wall-clock time through offset changes. */
public final class MeetingTimeRules {
 private MeetingTimeRules(){}
 public static ZoneId zone(String value){try{return ZoneId.of(value==null||value.isBlank()?"Asia/Kolkata":value);}catch(DateTimeException ex){throw new IllegalArgumentException("Choose a valid IANA meeting timezone");}}
 public static String safeUrl(String value){
  if(value==null||value.isBlank()||value.length()>500||value.chars().anyMatch(c->c<32))throw new IllegalArgumentException("Enter an approved HTTPS meeting link");
  try{URI uri=URI.create(value.trim());String host=uri.getHost()==null?"":uri.getHost().toLowerCase(Locale.ROOT);
   boolean approved=Set.of("meet.zoho.in","meet.zoho.com","meeting.zoho.com","meet.google.com","teams.microsoft.com","teams.live.com","teams.cloud.microsoft","zoom.us").contains(host)||host.endsWith(".zoom.us")||host.endsWith(".zoho.com");
   if(!"https".equalsIgnoreCase(uri.getScheme())||uri.getUserInfo()!=null||uri.getPort()!=-1||!approved)throw new IllegalArgumentException();return uri.toString();
  }catch(IllegalArgumentException ex){throw new IllegalArgumentException("Use an approved HTTPS meeting provider link");}
 }
 public record Occurrence(OffsetDateTime startsAt,OffsetDateTime endsAt){}
 public static Occurrence occurrence(OffsetDateTime start,OffsetDateTime end,String frequency,int offset,String timeZone){
  if(start==null||end==null||!end.isAfter(start)||Duration.between(start,end).compareTo(Duration.ofDays(2))>0||offset<0||offset>51)throw new IllegalArgumentException("Invalid recurrence times");
  ZonedDateTime local=start.atZoneSameInstant(zone(timeZone));
  ZonedDateTime shifted=switch(frequency){case "DAILY"->local.plusDays(offset);case "WEEKLY"->local.plusWeeks(offset);case "MONTHLY"->local.plusMonths(offset);default->throw new IllegalArgumentException("Unsupported recurrence");};
  return new Occurrence(shifted.toOffsetDateTime(),shifted.plus(Duration.between(start,end)).toOffsetDateTime());
 }
}
