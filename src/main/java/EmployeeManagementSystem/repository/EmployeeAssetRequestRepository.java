package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeAssetRequest; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployeeAssetRequestRepository extends JpaRepository<EmployeeAssetRequest,Long>{
 List<EmployeeAssetRequest> findByUserIdOrderByCreatedAtDesc(String userId);
 List<EmployeeAssetRequest> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndAssetTypeAndStatusIn(String userId,String assetType,List<String> statuses);
}
