package EmployeeManagementSystem.entity.chat;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="chat_members", uniqueConstraints=@UniqueConstraint(name="uk_chat_member",columnNames={"conversation_id","username"}), indexes=@Index(name="idx_chat_member_user",columnList="username"))
public class ChatMember {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="conversation_id",nullable=false) private Long conversationId;
 @Column(nullable=false,length=120) private String username;
 @Column(name="member_role",nullable=false,length=16) private String memberRole;
 @Column(name="joined_at",nullable=false) private Instant joinedAt;
 protected ChatMember(){}
 public ChatMember(Long conversationId,String username,String memberRole){this.conversationId=conversationId;this.username=username;this.memberRole=memberRole;this.joinedAt=Instant.now();}
 public Long getId(){return id;} public Long getConversationId(){return conversationId;} public String getUsername(){return username;} public String getMemberRole(){return memberRole;} public Instant getJoinedAt(){return joinedAt;}
}
