package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.LeaveDonationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LeaveDonationRequestRepository extends JpaRepository<LeaveDonationRequest,Long>{
    List<LeaveDonationRequest> findByDonorUserIdOrRecipientUserIdOrderByCreatedAtDesc(String donorUserId,String recipientUserId);
    List<LeaveDonationRequest> findAllByOrderByCreatedAtDesc();
    boolean existsByDonorUserIdAndRecipientUserIdAndStatusIn(String donorUserId,String recipientUserId,List<String> statuses);
}
