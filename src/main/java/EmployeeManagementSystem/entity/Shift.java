package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.Data; import java.time.LocalTime;
@Entity @Table(name="workforce_shifts") @Data public class Shift { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,unique=true) private String name; @Column(nullable=false) private LocalTime startTime; @Column(nullable=false) private LocalTime endTime; private Double standardHours=8.0; private String description; private Boolean overnight=false; private Boolean active=true; }
