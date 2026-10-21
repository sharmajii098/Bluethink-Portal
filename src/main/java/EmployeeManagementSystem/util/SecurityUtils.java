package EmployeeManagementSystem.util;

import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.repository.RegisterEmployeeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private final EmployeeRepository employeeRepository;
    private final RegisterEmployeeRepository registerEmployeeRepository;

    public SecurityUtils(EmployeeRepository employeeRepository, RegisterEmployeeRepository registerEmployeeRepository) {
        this.employeeRepository = employeeRepository;
        this.registerEmployeeRepository = registerEmployeeRepository;
    }

    /** Resolves the authenticated employee from the JWT principal, with email as a compatibility fallback. */
    public Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        String principal = authentication.getName();
        return employeeRepository.findByRegisterEmployee_UserId(principal)
                .orElseGet(() -> employeeRepository.findByEmail(principal)
                        .orElseThrow(() -> new IllegalStateException("Employee account not found: " + principal)));
    }
}
