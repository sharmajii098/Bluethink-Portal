package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/** Immutable after independent HR approval. New rules require a new reference/version. */
@Entity
@Table(name="approved_payroll_policies", uniqueConstraints=@UniqueConstraint(name="uq_approved_payroll_policy_reference", columnNames="policy_reference"))
public class ApprovedPayrollPolicy {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Version public Long version;
 @Column(name="policy_reference",nullable=false,length=160) public String reference;
 @Column(nullable=false,length=7) public String effectiveFrom;
 @Column(length=7) public String effectiveThrough;
 @Column(nullable=false,length=20) public String status="DRAFT";
 @Column(nullable=false,columnDefinition="LONGTEXT") public String policyJson;
 @Column(nullable=false,length=80) public String createdBy;
 @Column(length=80) public String approvedBy;
 public OffsetDateTime createdAt;
 public OffsetDateTime approvedAt;
}
