package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    boolean existsByCode(String code);

    Optional<LeaveType> findByName(String name);

    Optional<LeaveType> findByCode(String code);
}
