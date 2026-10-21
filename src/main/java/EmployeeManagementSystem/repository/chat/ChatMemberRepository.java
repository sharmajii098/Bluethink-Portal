package EmployeeManagementSystem.repository.chat;
import EmployeeManagementSystem.entity.chat.ChatMember; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List; import java.util.Optional;
public interface ChatMemberRepository extends JpaRepository<ChatMember,Long>{ List<ChatMember> findByUsername(String username); List<ChatMember> findByConversationId(Long id); Optional<ChatMember> findByConversationIdAndUsername(Long id,String username); boolean existsByConversationIdAndUsername(Long id,String username); long countByConversationId(Long id); }
