package EmployeeManagementSystem.entity.chat;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="chat_attachments",indexes={@Index(name="idx_chat_attachment_conversation",columnList="conversation_id"),@Index(name="idx_chat_attachment_scan",columnList="scan_status")})
public class ChatAttachment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="conversation_id",nullable=false) private Long conversationId;
 @Column(name="uploaded_by",nullable=false,length=120) private String uploadedBy;
 @Column(name="original_name",nullable=false,length=255) private String originalName;
 @Column(name="storage_name",nullable=false,length=80,unique=true) private String storageName;
 @Column(name="content_type",nullable=false,length=120) private String contentType;
 @Column(name="size_bytes",nullable=false) private long sizeBytes;
 @Column(name="sha256",nullable=false,length=64) private String sha256;
 @Column(name="scan_status",nullable=false,length=20) private String scanStatus;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 protected ChatAttachment(){} public ChatAttachment(Long c,String u,String n,String s,String t,long z,String h,String status){conversationId=c;uploadedBy=u;originalName=n;storageName=s;contentType=t;sizeBytes=z;sha256=h;scanStatus=status;createdAt=Instant.now();}
 public Long getId(){return id;} public Long getConversationId(){return conversationId;} public String getUploadedBy(){return uploadedBy;} public String getOriginalName(){return originalName;} public String getStorageName(){return storageName;} public String getContentType(){return contentType;} public long getSizeBytes(){return sizeBytes;} public String getSha256(){return sha256;} public String getScanStatus(){return scanStatus;} public Instant getCreatedAt(){return createdAt;}
}
