package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.TrainingCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TrainingCourseRepository extends JpaRepository<TrainingCourse,Long>{ List<TrainingCourse> findAllByOrderByStartDateDesc(); List<TrainingCourse> findByStatusOrderByStartDateDesc(String status); }
