package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "employee_assets", uniqueConstraints = @UniqueConstraint(name = "uk_asset_tag", columnNames = "assetTag"))
public class EmployeeAsset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String assetTag;
    @Column(nullable = false) private String assetName;
    @Column(nullable = false) private String assetType;
    private String brand;
    private String model;
    private String serialNumber;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;
    private String conditionStatus;
    private String assetStatus;
    private String location;
    private String assignedUserId;
    private String assignedEmployeeName;
    private LocalDateTime assignedAt;
    private LocalDateTime returnedAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist void pre(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; if(conditionStatus==null) conditionStatus="GOOD"; if(assetStatus==null) assetStatus="AVAILABLE"; }
    @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
