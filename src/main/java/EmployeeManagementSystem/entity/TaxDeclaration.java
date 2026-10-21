package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tax_declarations", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "financialYear"}))
@Getter @Setter @NoArgsConstructor
public class TaxDeclaration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String declarationNumber;
    @Column(nullable=false) private String userId;
    private String employeeName;
    @Column(nullable=false) private String financialYear;
    @Column(nullable=false) private String taxRegime = "NEW";
    private BigDecimal section80C = BigDecimal.ZERO;
    private BigDecimal section80D = BigDecimal.ZERO;
    private BigDecimal hraExemption = BigDecimal.ZERO;
    private BigDecimal homeLoanInterest = BigDecimal.ZERO;
    private BigDecimal otherDeductions = BigDecimal.ZERO;
    private BigDecimal totalDeclared = BigDecimal.ZERO;
    @Column(length=3000) private String proofReference;
    @Column(length=3000) private String employeeComment;
    @Column(nullable=false) private String status = "DRAFT";
    private String reviewerUserId;
    private String reviewerComment;
    private LocalDateTime reviewedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist void pre(){
        if(createdAt==null) createdAt=LocalDateTime.now(); updatedAt=createdAt;
        if(status==null) status="DRAFT"; if(taxRegime==null) taxRegime="NEW"; calculate();
    }
    @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); calculate(); }
    public void calculate(){
        totalDeclared = nz(section80C).add(nz(section80D)).add(nz(hraExemption)).add(nz(homeLoanInterest)).add(nz(otherDeductions));
    }
    private BigDecimal nz(BigDecimal v){ return v==null?BigDecimal.ZERO:v; }
}
