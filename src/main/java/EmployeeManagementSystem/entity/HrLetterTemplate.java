package EmployeeManagementSystem.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="hr_letter_templates",uniqueConstraints=@UniqueConstraint(name="uq_letter_template_revision",columnNames={"template_code","revision"}))
public class HrLetterTemplate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Version public Long version;
 @Column(name="template_code",nullable=false,length=60) public String templateCode;
 @Column(nullable=false) public int revision;
 @Column(nullable=false,length=40) public String letterType;
 @Column(nullable=false,length=140) public String title;
 @Column(nullable=false,length=200) public String companyName;
 @Column(nullable=false,length=1000) public String companyAddress;
 @Column(nullable=false,length=254) public String verificationEmail;
 @Column(length=80) public String cin;
 @Column(nullable=false,length=120) public String signatoryName;
 @Column(nullable=false,length=120) public String signatoryDesignation;
 @Column(nullable=false,columnDefinition="LONGTEXT") public String body;
 @Column(nullable=false,length=20) public String status="DRAFT";
 @Column(nullable=false,length=80) public String createdBy;
 @Column(length=80) public String approvedBy;
 public OffsetDateTime createdAt;
 public OffsetDateTime approvedAt;
}
