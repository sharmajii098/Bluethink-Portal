// SalaryStructureServiceImpl.java
package EmployeeManagementSystem.service;

import EmployeeManagementSystem.dto.SalaryStructureDTO;
import EmployeeManagementSystem.entity.EmployeeProfile;
import EmployeeManagementSystem.entity.SalaryStructure;
import EmployeeManagementSystem.repository.EmployeeProfileRepository;
import EmployeeManagementSystem.repository.AdminSalaryStructureRepo;
import EmployeeManagementSystem.service.SalaryStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final AdminSalaryStructureRepo salaryStructureRepo;
    private final EmployeeProfileRepository employeeProfileRepository;

    @Override
    public SalaryStructure saveSalaryStructure(SalaryStructureDTO dto) {
        validate(dto);
        if (salaryStructureRepo.existsByEmployeeProfileId(dto.getEmployeeProfileId())) {
            throw new RuntimeException("Salary structure already exists for this employee");
        }
        EmployeeProfile employeeProfile = employeeProfileRepository.findById(dto.getEmployeeProfileId())
                .orElseThrow(() -> new RuntimeException("Employee Profile not found with id: " + dto.getEmployeeProfileId()));

        SalaryStructure salaryStructure = new SalaryStructure();
        salaryStructure.setEmployeeProfileId(employeeProfile.getId());
        salaryStructure.setEmployeeProfile(employeeProfile);
        salaryStructure.setBasicSalary(dto.getBasicSalary());
        salaryStructure.setHra(dto.getHra());
        salaryStructure.setConveyance(dto.getConveyance());
        salaryStructure.setMedicalAllowance(dto.getMedicalAllowance());
        salaryStructure.setSpecialAllowance(dto.getSpecialAllowance());
        salaryStructure.setOtherAllowance(dto.getOtherAllowance());
        salaryStructure.setPf(dto.getPf());
        salaryStructure.setEsi(dto.getEsi());
        salaryStructure.setProfessionalTax(dto.getProfessionalTax());
        salaryStructure.setTds(dto.getTds());
        salaryStructure.setLoanDeduction(dto.getLoanDeduction());
        salaryStructure.setEffectiveFrom(dto.getEffectiveFrom());

        return salaryStructureRepo.save(salaryStructure);
    }

    @Override
    public SalaryStructure updateSalaryStructure(SalaryStructureDTO dto) {
        validate(dto);
        SalaryStructure existing = salaryStructureRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Salary structure not found with id: " + dto.getId()));

        existing.setBasicSalary(dto.getBasicSalary());
        existing.setHra(dto.getHra());
        existing.setConveyance(dto.getConveyance());
        existing.setMedicalAllowance(dto.getMedicalAllowance());
        existing.setSpecialAllowance(dto.getSpecialAllowance());
        existing.setOtherAllowance(dto.getOtherAllowance());
        existing.setPf(dto.getPf());
        existing.setEsi(dto.getEsi());
        existing.setProfessionalTax(dto.getProfessionalTax());
        existing.setTds(dto.getTds());
        existing.setLoanDeduction(dto.getLoanDeduction());
        existing.setEffectiveFrom(dto.getEffectiveFrom());

        return salaryStructureRepo.save(existing);
    }

    @Override
    public SalaryStructure getSalaryStructure(Long id) {
        return salaryStructureRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary structure not found with id: " + id));
    }

    @Override
    public SalaryStructure getSalaryStructureByEmployeeProfileId(Long profileId) {
        return salaryStructureRepo.findByEmployeeProfileId(profileId)
                .orElseThrow(() -> new RuntimeException("Salary structure not found for profile id: " + profileId));
    }

    @Override
    public List<SalaryStructure> getAllSalaryStructures() {
        return salaryStructureRepo.findAll();
    }

    @Override
    public void deleteSalaryStructure(Long id) {
        if (!salaryStructureRepo.existsById(id)) throw new RuntimeException("Salary structure not found with id: " + id);
        salaryStructureRepo.deleteById(id);
    }

    private void validate(SalaryStructureDTO dto) {
        if (dto == null || dto.getEmployeeProfileId() == null) {
            throw new IllegalArgumentException("Employee is required");
        }

        // Normalise null monetary values so the persisted structure never silently
        // turns a partially filled form into an all-zero salary structure.
        if (dto.getBasicSalary() == null) dto.setBasicSalary(BigDecimal.ZERO);
        if (dto.getHra() == null) dto.setHra(BigDecimal.ZERO);
        if (dto.getConveyance() == null) dto.setConveyance(BigDecimal.ZERO);
        if (dto.getMedicalAllowance() == null) dto.setMedicalAllowance(BigDecimal.ZERO);
        if (dto.getSpecialAllowance() == null) dto.setSpecialAllowance(BigDecimal.ZERO);
        if (dto.getOtherAllowance() == null) dto.setOtherAllowance(BigDecimal.ZERO);
        if (dto.getPf() == null) dto.setPf(BigDecimal.ZERO);
        if (dto.getEsi() == null) dto.setEsi(BigDecimal.ZERO);
        if (dto.getProfessionalTax() == null) dto.setProfessionalTax(BigDecimal.ZERO);
        if (dto.getTds() == null) dto.setTds(BigDecimal.ZERO);
        if (dto.getLoanDeduction() == null) dto.setLoanDeduction(BigDecimal.ZERO);

        BigDecimal[] values = {dto.getBasicSalary(), dto.getHra(), dto.getConveyance(),
                dto.getMedicalAllowance(), dto.getSpecialAllowance(), dto.getOtherAllowance(),
                dto.getPf(), dto.getEsi(), dto.getProfessionalTax(), dto.getTds(), dto.getLoanDeduction()};
        for (BigDecimal value : values) {
            if (value.signum() < 0) {
                throw new IllegalArgumentException("Salary components cannot be negative");
            }
        }

        BigDecimal earnings = dto.getBasicSalary()
                .add(dto.getHra())
                .add(dto.getConveyance())
                .add(dto.getMedicalAllowance())
                .add(dto.getSpecialAllowance())
                .add(dto.getOtherAllowance());
        if (earnings.signum() <= 0) {
            throw new IllegalArgumentException("At least one earning amount is required; Basic Salary cannot be zero for a payroll salary structure");
        }
    }

    @Override
    public boolean existsByEmployeeProfileId(Long profileId) {
        return salaryStructureRepo.existsByEmployeeProfileId(profileId);
    }
}