package EmployeeManagementSystem.entity.chat;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="chat_read_receipts", uniqueConstraints=@UniqueConstraint(name="uk_chat_read_message_user", columnNames={"message_id","username"}), indexes=@Index(name="idx_chat_receipt_user",columnList="username"))
public class ChatReadReceipt {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="message_id",nullable=false) private Long messageId;
 @Column(nullable=false,length=120) private String username;
 @Column(name="read_at",nullable=false) private Instant readAt;
 protected ChatReadReceipt(){} public ChatReadReceipt(Long messageId,String username){this.messageId=messageId;this.username=username;this.readAt=Instant.now();}
 public Long getId(){return id;} public Long getMessageId(){return messageId;} public String getUsername(){return username;} public Instant getReadAt(){return readAt;}
}
