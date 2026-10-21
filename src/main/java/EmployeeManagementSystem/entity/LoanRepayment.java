package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.Data; import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Data @Table(name="loan_repayments")
public class LoanRepayment { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) private LoanAdvance loan; private Integer installmentNumber; private LocalDate dueDate; private BigDecimal amount; private LocalDate paidDate; private String status; }
