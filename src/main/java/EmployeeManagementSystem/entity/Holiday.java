package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "holidays", uniqueConstraints = @UniqueConstraint(columnNames = {"holidayDate", "name", "location"}))
@Getter @Setter @NoArgsConstructor
public class Holiday {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private LocalDate holidayDate;
    @Column(nullable=false) private String name;
    private String description;
    @Column(nullable=false) private String holidayType = "PUBLIC";
    private String location = "ALL";
    private Boolean optionalHoliday = false;
    private Boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist void pre(){ if(createdAt==null) createdAt=LocalDateTime.now(); updatedAt=createdAt; if(holidayType==null) holidayType="PUBLIC"; if(location==null||location.isBlank()) location="ALL"; if(optionalHoliday==null) optionalHoliday=false; if(active==null) active=true; }
    @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
