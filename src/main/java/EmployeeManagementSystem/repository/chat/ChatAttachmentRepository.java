package EmployeeManagementSystem.repository.chat;
import EmployeeManagementSystem.entity.chat.ChatAttachment; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ChatAttachmentRepository extends JpaRepository<ChatAttachment,Long>{ List<ChatAttachment> findByConversationIdOrderByCreatedAtDesc(Long conversationId); }
