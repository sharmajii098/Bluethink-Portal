package EmployeeManagementSystem.service;
import EmployeeManagementSystem.repository.CalendarEventLinkRepository;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.data.domain.PageRequest;
import java.time.OffsetDateTime;
@Component
public class CalendarSyncWorker {
 private final CalendarEventLinkRepository links;private final CalendarSyncService sync;
 public CalendarSyncWorker(CalendarEventLinkRepository links,CalendarSyncService sync){this.links=links;this.sync=sync;}
 @Scheduled(fixedDelayString="${bluethink.calendar.sync.poll-ms:60000}") public void poll(){
  for(Long id:links.due(OffsetDateTime.now(),PageRequest.of(0,30)))try{sync.sync(id);}catch(RuntimeException ignored){/* Next poll retries a rolled-back transaction. No tokens or personal data are logged. */}
 }
}
