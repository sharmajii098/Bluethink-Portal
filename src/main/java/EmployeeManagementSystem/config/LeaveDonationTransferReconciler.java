package EmployeeManagementSystem.config;

import EmployeeManagementSystem.service.LeaveDonationRequestService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * V8.7.3 compatibility repair.
 * Older versions marked leave-donation requests APPROVED without changing
 * either employee's LeaveBalance. On startup, apply each such approval once.
 */
@Component
public class LeaveDonationTransferReconciler implements ApplicationRunner {
    private final LeaveDonationRequestService service;

    public LeaveDonationTransferReconciler(LeaveDonationRequestService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        service.reconcileApprovedTransfers();
    }
}
