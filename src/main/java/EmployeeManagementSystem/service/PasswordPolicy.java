package EmployeeManagementSystem.service;
import java.nio.charset.StandardCharsets;
/** Shared policy compatible with the configured BCrypt encoder. */
public final class PasswordPolicy {
    private PasswordPolicy() { }
    public static boolean valid(String password) {
        return password != null && password.length() >= 12
                && password.getBytes(StandardCharsets.UTF_8).length <= 72
                && password.matches("(?s).*[A-Za-z].*") && password.matches("(?s).*\\d.*");
    }
}
