package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.EmployeeAsset;
import java.util.List;

public interface EmployeeAssetService {
    List<EmployeeAsset> my(String userId);
    List<EmployeeAsset> all();
    EmployeeAsset create(EmployeeAsset asset);
    EmployeeAsset update(Long id, EmployeeAsset asset);
    EmployeeAsset assign(Long id, String userId, String employeeName);
    EmployeeAsset returnAsset(Long id);
    EmployeeAsset markMaintenance(Long id);
    EmployeeAsset markAvailable(Long id);
    void delete(Long id);
}
