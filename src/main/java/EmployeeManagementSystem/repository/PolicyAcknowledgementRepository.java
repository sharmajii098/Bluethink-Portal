package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.PolicyAcknowledgement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PolicyAcknowledgementRepository extends JpaRepository<PolicyAcknowledgement,Long> {
    Optional<PolicyAcknowledgement> findByPolicyIdAndUserId(Long policyId, String userId);
    long countByPolicyIdAndAcknowledgedTrue(Long policyId);
    void deleteByPolicyId(Long policyId);
}
