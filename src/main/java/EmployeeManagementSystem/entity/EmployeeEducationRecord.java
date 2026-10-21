package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_education_records", indexes = {
        @Index(name = "idx_education_employee_id", columnList = "employee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEducationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 80)
    private String employeeId;

    @Column(nullable = false, length = 60)
    private String level;

    @Column(nullable = false, length = 180)
    private String institution;

    @Column(name = "board_university", length = 180)
    private String boardUniversity;

    @Column(name = "course_stream", length = 160)
    private String courseStream;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(length = 20)
    private String passingYear;

    @Column(length = 50)
    private String score;

    @Column(name = "certificate_document_id")
    private Long certificateDocumentId;

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
