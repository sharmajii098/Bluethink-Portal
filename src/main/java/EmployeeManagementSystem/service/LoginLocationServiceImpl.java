package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.LoginLocation;
import EmployeeManagementSystem.repository.LoginLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class LoginLocationServiceImpl implements LoginLocationService {
    private final LoginLocationRepository repository;
    public void save(LoginLocation x) { repository.save(x); }
    public List<LoginLocation> getAllLoginLocations() { return repository.findAll(); }
    public List<LoginLocation> getByEmployeeId(String employeeId) { return repository.findByEmployeeIdOrderByLoginTimeDesc(employeeId); }
    public void delete(Long id) { repository.deleteById(id); }
}
