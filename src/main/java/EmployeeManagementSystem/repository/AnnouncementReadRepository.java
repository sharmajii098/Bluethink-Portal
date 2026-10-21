package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface AnnouncementReadRepository extends JpaRepository<AnnouncementRead,Long>{
 Optional<AnnouncementRead> findByAnnouncementIdAndUserId(Long announcementId,String userId);
 List<AnnouncementRead> findByUserId(String userId);
}
