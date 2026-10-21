// SalaryStructureService.java
package EmployeeManagementSystem.service;

import EmployeeManagementSystem.dto.SalaryStructureDTO;
import EmployeeManagementSystem.entity.SalaryStructure;

import java.util.List;

public interface SalaryStructureService {

    SalaryStructure saveSalaryStructure(SalaryStructureDTO dto);

    SalaryStructure updateSalaryStructure(SalaryStructureDTO dto);

    SalaryStructure getSalaryStructure(Long id);

    SalaryStructure getSalaryStructureByEmployeeProfileId(Long profileId);

    List<SalaryStructure> getAllSalaryStructures();

    void deleteSalaryStructure(Long id);

    boolean existsByEmployeeProfileId(Long profileId);
}