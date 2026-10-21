package EmployeeManagementSystem.config;

import EmployeeManagementSystem.entity.LeaveType;
import EmployeeManagementSystem.repository.LeaveTypeRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ensures the employee-facing leave catalogue always contains the three
 * Bluethink leave types used by the React portal. Existing records are kept;
 * this initializer only creates missing entries and re-enables the canonical
 * records if they were disabled accidentally.
 */
@Component
public class LeaveTypeDataInitializer implements ApplicationRunner {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeDataInitializer(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensure("Casual Leave", "CASUAL_LEAVE", 10);
        ensure("Project Off", "PROJECT_OFF", 0);
        ensure("Leave Without Pay", "LEAVE_WITHOUT_PAY", 0);
    }

    private void ensure(String name, String code, int defaultDays) {
        LeaveType type = leaveTypeRepository.findByName(name).orElse(null);
        if (type == null) {
            type = new LeaveType();
            type.setName(name);
            type.setCode(code);
            type.setDefaultDays(defaultDays);
            type.setIsActive(true);
            leaveTypeRepository.save(type);
            return;
        }

        boolean changed = false;
        if (!code.equals(type.getCode())) {
            type.setCode(code);
            changed = true;
        }
        if (type.getDefaultDays() == null) {
            type.setDefaultDays(defaultDays);
            changed = true;
        }
        if (Boolean.FALSE.equals(type.getIsActive())) {
            type.setIsActive(true);
            changed = true;
        }
        if (changed) leaveTypeRepository.save(type);
    }
}
