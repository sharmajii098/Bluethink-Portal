package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SupportTicketRepository extends JpaRepository<SupportTicket,Long>{
 List<SupportTicket> findByUserIdOrderByUpdatedAtDesc(String userId);
 List<SupportTicket> findAllByOrderByUpdatedAtDesc();
 Optional<SupportTicket> findTopByOrderByIdDesc();
 long countByStatus(String status);
}
