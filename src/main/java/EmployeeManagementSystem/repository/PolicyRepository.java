package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PolicyRepository extends JpaRepository<Policy,Long> {
    List<Policy> findAllByOrderByEffectiveDateDesc();
}
