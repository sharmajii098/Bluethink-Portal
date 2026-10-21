package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.TravelRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TravelRequestRepository extends JpaRepository<TravelRequest,Long>{
 List<TravelRequest> findByUserIdOrderByStartDateDesc(String userId);
 List<TravelRequest> findAllByOrderByStartDateDesc();
}
