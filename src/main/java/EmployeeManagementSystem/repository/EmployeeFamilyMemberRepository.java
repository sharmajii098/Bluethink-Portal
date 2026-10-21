package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.EmployeeFamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeFamilyMemberRepository extends JpaRepository<EmployeeFamilyMember, Long> {
    List<EmployeeFamilyMember> findByEmployeeIdOrderByIdAsc(String employeeId);
    Optional<EmployeeFamilyMember> findByIdAndEmployeeId(Long id, String employeeId);
}
