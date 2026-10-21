package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.StandupUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; import java.util.List; import java.util.Optional;
public interface StandupUpdateRepository extends JpaRepository<StandupUpdate,Long>{
 Optional<StandupUpdate> findByEmployeeUsernameAndWorkDate(String username, LocalDate date);
 List<StandupUpdate> findByEmployeeUsernameOrderByWorkDateDesc(String username);
 List<StandupUpdate> findAllByOrderByWorkDateDescUpdatedAtDesc();
}
