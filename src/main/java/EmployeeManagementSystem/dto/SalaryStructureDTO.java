// SalaryStructureDTO.java
package EmployeeManagementSystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructureDTO {
 private Long id;
 @JsonProperty("employeeProfileId")
 private Long employeeProfileId;  // Changed from employeeId to employeeProfileId
 @JsonProperty("basicSalary")
 private BigDecimal basicSalary = BigDecimal.ZERO;
 @JsonProperty("hra")
 private BigDecimal hra = BigDecimal.ZERO;
 @JsonProperty("conveyance")
 private BigDecimal conveyance = BigDecimal.ZERO;
 @JsonProperty("medicalAllowance")
 private BigDecimal medicalAllowance = BigDecimal.ZERO;
 @JsonProperty("specialAllowance")
 private BigDecimal specialAllowance = BigDecimal.ZERO;
 @JsonProperty("otherAllowance")
 private BigDecimal otherAllowance = BigDecimal.ZERO;
 @JsonProperty("pf")
 private BigDecimal pf = BigDecimal.ZERO;
 @JsonProperty("esi")
 private BigDecimal esi = BigDecimal.ZERO;
 @JsonProperty("professionalTax")
 private BigDecimal professionalTax = BigDecimal.ZERO;
 @JsonProperty("tds")
 private BigDecimal tds = BigDecimal.ZERO;
 @JsonProperty("loanDeduction")
 private BigDecimal loanDeduction = BigDecimal.ZERO;
 @JsonProperty("effectiveFrom")
 private LocalDate effectiveFrom;
}