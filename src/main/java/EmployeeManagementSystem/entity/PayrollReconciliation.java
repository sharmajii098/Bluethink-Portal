package EmployeeManagementSystem.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="payroll_reconciliations",uniqueConstraints=@UniqueConstraint(name="uq_payroll_reconciliation_period",columnNames={"profile_id","period"}))
public class PayrollReconciliation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Version public Long version;
 @Column(name="profile_id",nullable=false) public Long profileId;
 @Column(nullable=false,length=80) public String employeeUserId;
 @Column(nullable=false,length=7) public String period;
 @Column(nullable=false,length=20) public String status="DRAFT";
 @Column(nullable=false,columnDefinition="LONGTEXT") public String inputJson;
 @Column(nullable=false,columnDefinition="LONGTEXT") public String reportJson;
 @Column(nullable=false,length=64) public String sourceFingerprint;
 @Column(nullable=false,length=80) public String createdBy;
 @Column(length=80) public String approvedBy;
 public OffsetDateTime createdAt;
 public OffsetDateTime approvedAt;
 public OffsetDateTime consumedAt;
}
