package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.HrLetterTemplate;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface HrLetterTemplateRepository extends JpaRepository<HrLetterTemplate,Long>{
 List<HrLetterTemplate> findAllByOrderByCreatedAtDesc();
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select t from HrLetterTemplate t where t.id=:id") Optional<HrLetterTemplate> lock(@Param("id") Long id);
}
