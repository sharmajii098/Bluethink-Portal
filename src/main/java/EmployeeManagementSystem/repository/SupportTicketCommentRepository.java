package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.SupportTicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SupportTicketCommentRepository extends JpaRepository<SupportTicketComment,Long>{List<SupportTicketComment> findByTicket_IdOrderByCreatedAtAsc(Long ticketId);}
