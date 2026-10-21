package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.CalendarEventLink;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.OffsetDateTime;
public interface CalendarEventLinkRepository extends JpaRepository<CalendarEventLink,Long> {
 List<CalendarEventLink> findByConnectionOwnerId(String ownerId);
 Optional<CalendarEventLink> findByConnectionIdAndMeetingId(Long connectionId,Long meetingId);
 void deleteByConnectionId(Long connectionId);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select l from CalendarEventLink l join fetch l.meeting join fetch l.connection where l.id=:id")
 Optional<CalendarEventLink> lock(@Param("id") Long id);
 @Query("select l.id from CalendarEventLink l where l.connection.status='CONNECTED' and l.status<>'UNSUBSCRIBED' and (l.status='PENDING' or l.status='UNSUBSCRIBE_PENDING' or (l.status='UNSUBSCRIBE_RETRY' and l.nextAttemptAt<=:now) or (l.attemptedSequence<>l.meeting.calendarSequence and l.status not in ('RETRY','FAILED','UNSUBSCRIBE_RETRY','UNSUBSCRIBE_FAILED')) or (l.status='RETRY' and l.nextAttemptAt<=:now)) order by l.id")
 List<Long> due(@Param("now") OffsetDateTime now,org.springframework.data.domain.Pageable page);
}
