package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.PortalMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;
public interface PortalMeetingRepository extends JpaRepository<PortalMeeting,Long>{
 @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @org.springframework.data.jpa.repository.Query("select m from PortalMeeting m where m.id=:id")
 java.util.Optional<PortalMeeting> lock(@org.springframework.data.repository.query.Param("id") Long id);
 List<PortalMeeting> findBySeriesIdOrderByStartsAtAsc(String seriesId);
 List<PortalMeeting> findByStartsAtGreaterThanEqualAndStartsAtLessThanOrderByStartsAtAsc(OffsetDateTime from,OffsetDateTime to);
}
