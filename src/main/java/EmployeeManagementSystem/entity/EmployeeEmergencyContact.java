package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name="employee_emergency_contacts")
@Getter @Setter @NoArgsConstructor
public class EmployeeEmergencyContact {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String recordNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String contactType="EMERGENCY";
 @Column(nullable=false) private String contactName;
 private String relationship;
 @Column(nullable=false) private String phone;
 private String alternatePhone;
 private String email;
 @Column(length=2000) private String address;
 private Boolean primaryContact=false;
 private Boolean verified=false;
 private LocalDate dateOfBirth;
 private Boolean financiallyDependent=false;
 private String notes;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(contactType==null)contactType="EMERGENCY";if(primaryContact==null)primaryContact=false;if(verified==null)verified=false;if(financiallyDependent==null)financiallyDependent=false;}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
