package EmployeeManagementSystem.dto;

import EmployeeManagementSystem.entity.SalaryStructure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Flat API response for salary structures.
 * Deliberately excludes the JPA employeeProfile relationship to prevent
 * recursive/deep JSON serialization of the employee object graph.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructureResponseDTO {
    private Long id;
    private Long employeeProfileId;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal conveyance;
    private BigDecimal medicalAllowance;
    private BigDecimal specialAllowance;
    private BigDecimal otherAllowance;
    private BigDecimal pf;
    private BigDecimal esi;
    private BigDecimal professionalTax;
    private BigDecimal tds;
    private BigDecimal loanDeduction;
    private LocalDate effectiveFrom;

    public static SalaryStructureResponseDTO fromEntity(SalaryStructure s) {
        if (s == null) return null;
        return new SalaryStructureResponseDTO(
                s.getId(),
                s.getEmployeeProfileId(),
                s.getBasicSalary(),
                s.getHra(),
                s.getConveyance(),
                s.getMedicalAllowance(),
                s.getSpecialAllowance(),
                s.getOtherAllowance(),
                s.getPf(),
                s.getEsi(),
                s.getProfessionalTax(),
                s.getTds(),
                s.getLoanDeduction(),
                s.getEffectiveFrom()
        );
    }
}
