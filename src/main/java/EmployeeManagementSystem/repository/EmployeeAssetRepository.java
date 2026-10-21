package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.EmployeeAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmployeeAssetRepository extends JpaRepository<EmployeeAsset, Long> {
    Optional<EmployeeAsset> findByAssetTag(String assetTag);
    List<EmployeeAsset> findByAssignedUserIdOrderByUpdatedAtDesc(String assignedUserId);
    List<EmployeeAsset> findAllByOrderByUpdatedAtDesc();
}
