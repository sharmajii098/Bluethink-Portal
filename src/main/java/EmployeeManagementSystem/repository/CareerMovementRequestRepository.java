package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.CareerMovementRequest; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CareerMovementRequestRepository extends JpaRepository<CareerMovementRequest,Long>{List<CareerMovementRequest> findByEmployeeUserIdOrderByCreatedAtDesc(String userId);List<CareerMovementRequest> findAllByOrderByCreatedAtDesc();}
