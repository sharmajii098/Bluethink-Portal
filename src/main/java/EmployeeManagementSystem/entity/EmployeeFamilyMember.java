package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_family_members", indexes = {
        @Index(name = "idx_family_employee_id", columnList = "employee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeFamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 80)
    private String employeeId;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(nullable = false, length = 60)
    private String relationship;

    private LocalDate dob;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(length = 120)
    private String occupation;

    private boolean dependent;

    @Column(name = "emergency_contact")
    private boolean emergencyContact;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void created() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void updated() {
        updatedAt = LocalDateTime.now();
    }
}
