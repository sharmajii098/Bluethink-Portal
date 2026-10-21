package EmployeeManagementSystem.repository.chat;
import EmployeeManagementSystem.entity.chat.ChatReadReceipt; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ChatReadReceiptRepository extends JpaRepository<ChatReadReceipt,Long>{ boolean existsByMessageIdAndUsername(Long messageId,String username); List<ChatReadReceipt> findByMessageIdIn(Collection<Long> ids); List<ChatReadReceipt> findByMessageId(Long id); void deleteByMessageId(Long id); }
