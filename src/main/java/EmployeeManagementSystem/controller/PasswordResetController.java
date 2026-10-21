package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

/** Public reset API. All request responses are identical to prevent account enumeration. */
@RestController
public class PasswordResetController {
    private final PasswordResetService service;
    private final String frontendUrl;

    public PasswordResetController(PasswordResetService service,
                                   @Value("${portal.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.service = service; this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

    @PostMapping("/api/public/password-reset/request")
    public Map<String, String> request(@RequestBody Map<String, String> body) {
        PasswordResetService.ResetMail mail = service.prepare(body == null ? null : body.get("email"));
        // prepare() has committed before delivery begins: no sending an uncommitted reset link.
        service.deliver(mail);
        return Map.of("message", "If this email belongs to an account, a reset link will be sent. Check Inbox and Spam.");
    }

    @PostMapping("/api/public/password-reset/validate")
    public Map<String, Boolean> validate(@RequestBody Map<String, String> body) {
        return Map.of("valid", service.valid(body == null ? null : body.get("token")));
    }

    @PostMapping("/api/public/password-reset/confirm")
    public Map<String, Object> confirm(@RequestBody Map<String, String> body) {
        if (body == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request.");
        service.reset(body.get("token"), body.get("password"));
        return Map.of("success", true, "message", "Password changed. Sign in again on all devices.");
    }

    /** Existing bookmarked backend URL redirects to the real React screen instead of a 404. */
    @GetMapping("/auth/forgot-password")
    public RedirectView legacyForgotPassword() {
        RedirectView redirect = new RedirectView(frontendUrl + "/forgot-password");
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @GetMapping("/auth/reset-password")
    public RedirectView legacyResetPassword() {
        RedirectView redirect = new RedirectView(frontendUrl + "/forgot-password");
        redirect.setExposeModelAttributes(false);
        return redirect;
    }
}
