package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.*; import java.util.*;
public interface SupportTicketService {
 List<SupportTicket> my(String userId); List<SupportTicket> all(); SupportTicket create(SupportTicket t,String userId,String name); SupportTicket update(Long id,SupportTicket t,String userId,String role); SupportTicket changeStatus(Long id,String status,String resolution,String actor,String role); SupportTicket assign(Long id,String assignee,String team); List<SupportTicketComment> comments(Long id); SupportTicketComment addComment(Long id,String userId,String name,String text,String role); void delete(Long id,String userId,String role); SupportTicket get(Long id);
}
