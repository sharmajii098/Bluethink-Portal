package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.MeetingReminder;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.OffsetDateTime;
import java.util.*;
public interface MeetingReminderRepository extends JpaRepository<MeetingReminder,Long> {
    List<MeetingReminder> findByMeetingIdOrderBySendAtAsc(Long meetingId);
    boolean existsByMeetingIdAndRecipientAndDeliveryKey(Long meetingId,String recipient,String deliveryKey);
    @Query("select r.id from MeetingReminder r where r.status='PENDING' and r.sendAt<=:now and (r.nextAttemptAt is null or r.nextAttemptAt<=:now) order by r.sendAt")
    List<Long> due(@Param("now") OffsetDateTime now,org.springframework.data.domain.Pageable page);
    @Query("select r.id from MeetingReminder r where r.status='SENDING' and r.claimedAt<:cutoff")
    List<Long> stale(@Param("cutoff") OffsetDateTime cutoff,org.springframework.data.domain.Pageable page);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from MeetingReminder r join fetch r.meeting where r.id=:id")
    Optional<MeetingReminder> lock(@Param("id") Long id);
}
