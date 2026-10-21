package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_employment_history", indexes = {
        @Index(name = "idx_employment_history_employee_id", columnList = "employee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEmploymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 80)
    private String employeeId;

    @Column(name = "company_name", nullable = false, length = 180)
    private String companyName;

    @Column(nullable = false, length = 140)
    private String designation;

    @Column(name = "employment_type", length = 80)
    private String employmentType;

    @Column(length = 160)
    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "last_drawn_salary", precision = 14, scale = 2)
    private BigDecimal lastDrawnSalary;

    @Column(name = "reason_for_leaving", columnDefinition = "TEXT")
    private String reasonForLeaving;

    @Column(name = "offer_letter_document_id")
    private Long offerLetterDocumentId;

    @Column(name = "salary_slip_document_id")
    private Long salarySlipDocumentId;

    @Column(name = "experience_letter_document_id")
    private Long experienceLetterDocumentId;

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
