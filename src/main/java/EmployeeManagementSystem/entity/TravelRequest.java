package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @Table(name="travel_requests")
public class TravelRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String purpose;
 @Column(nullable=false) private String destination;
 @Column(nullable=false) private LocalDate startDate;
 @Column(nullable=false) private LocalDate endDate;
 private String travelMode;
 private String itinerary;
 private Double estimatedCost;
 private Double advanceAmount;
 private String projectName;
 private String status;
 private String managerComment;
 private String reviewedBy;
 private LocalDateTime reviewedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="PENDING";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
