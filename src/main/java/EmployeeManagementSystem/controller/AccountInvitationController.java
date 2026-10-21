package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import EmployeeManagementSystem.service.AccountInvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class AccountInvitationController {
    private final AccountInvitationService service;
    private final EmployeeProfileRepository profiles;
    public AccountInvitationController(AccountInvitationService service, EmployeeProfileRepository profiles) {
        this.service = service; this.profiles = profiles;
    }
    @GetMapping("/public/invitations/validate")
    public Map<String, Object> validate(@RequestParam String token) { return Map.of("valid", service.valid(token)); }
    @PostMapping("/public/invitations/activate")
    public Map<String, Object> activate(@RequestBody Map<String, String> body) {
        service.activate(body.get("token"), body.get("password"));
        return Map.of("success", true, "message", "Password configured. You can sign in now.");
    }
    @PostMapping("/employees/{id}/resend-invitation")
    @Transactional
    public Map<String, Object> resend(@PathVariable Long id, Authentication auth) {
        String role = auth == null ? "" : auth.getAuthorities().stream().map(Object::toString)
            .filter(x -> x.startsWith("ROLE_")).findFirst().orElse("");
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_HR"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only Admin or HR can resend invitations.");
        EmployeeProfile profile = profiles.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee not found."));
        RegisterEmployee account = profile.getEmployee() == null ? null : profile.getEmployee().getRegisterEmployee();
        if (account == null || account.getEmail() == null || account.getEmail().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee has no account email.");
        String token = service.issue(account);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("deliveryStatus", "PENDING");
        result.put("message", "Invitation scheduled after database commit.");
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                boolean accepted = service.send(account, token);
                result.put("deliveryStatus", accepted ? "SMTP_ACCEPTED" : "FAILED");
                result.put("message", accepted ? "Gmail accepted the invitation. Inbox delivery is not guaranteed."
                    : "Gmail could not send the invitation. Check SMTP configuration and try Resend Invitation.");
            }
        });
        return result;
    }
}
