package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.TrainingEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingEnrollmentRepository
        extends JpaRepository<TrainingEnrollment, Long> {

    List<TrainingEnrollment> findByUserIdOrderByEnrolledAtDesc(String userId);

    Optional<TrainingEnrollment> findByCourseIdAndUserId(
            Long courseId,
            String userId
    );

    List<TrainingEnrollment> findByCourseIdOrderByEnrolledAtDesc(
            Long courseId
    );

    long countByStatus(String status);

    // ADD THIS
    List<TrainingEnrollment> findAllByOrderByEnrolledAtDesc();
}