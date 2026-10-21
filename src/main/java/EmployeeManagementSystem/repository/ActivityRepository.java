package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {
    Page<Activity> findByEmployeeNameContainingIgnoreCaseOrActivityContainingIgnoreCase(String employeeName, String activity, Pageable pageable);
    Page<Activity> findByStatusIgnoreCase(String status, Pageable pageable);
    Page<Activity> findByEmployeeNameContainingIgnoreCaseAndStatusIgnoreCase(String employeeName, String status, Pageable pageable);
}
