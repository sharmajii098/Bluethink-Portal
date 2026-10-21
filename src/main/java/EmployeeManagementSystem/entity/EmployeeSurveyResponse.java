package EmployeeManagementSystem.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import lombok.Data; import java.time.LocalDateTime;
@Entity @Data @Table(name="employee_survey_responses", uniqueConstraints=@UniqueConstraint(columnNames={"survey_id","userId"})) public class EmployeeSurveyResponse {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="survey_id") private EmployeeSurvey survey;
 @JsonIgnore @Column(nullable=false) private String userId; private String employeeName; private Integer rating; @Column(length=2000) private String comment; private LocalDateTime submittedAt;
 @PrePersist void pre(){if(submittedAt==null)submittedAt=LocalDateTime.now();}
}
