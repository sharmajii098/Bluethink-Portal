package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.MentorshipAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorshipAssignmentRepository extends JpaRepository<MentorshipAssignment, Long> {
    List<MentorshipAssignment> findByMentor_IdOrderByCreatedAtDesc(Long mentorId);
    List<MentorshipAssignment> findByMentee_IdOrderByCreatedAtDesc(Long menteeId);
    List<MentorshipAssignment> findByManager_IdOrderByCreatedAtDesc(Long managerId);
    List<MentorshipAssignment> findByMentor_IdAndStatusIgnoreCaseOrderByCreatedAtDesc(Long mentorId, String status);
    List<MentorshipAssignment> findByMentee_IdAndStatusIgnoreCaseOrderByCreatedAtDesc(Long menteeId, String status);
    List<MentorshipAssignment> findByManager_IdAndStatusIgnoreCaseOrderByCreatedAtDesc(Long managerId, String status);
}
