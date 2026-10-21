package EmployeeManagementSystem.service;
import java.util.Locale;
/** Server policy: personal staff requests require an independent Admin decision. */
public final class StaffRequestPolicy {
    private StaffRequestPolicy() { }
    public static boolean canReview(String reviewerRole, String requesterRole, boolean directReport, boolean self) {
        if (self) return false;
        String reviewer = normalize(reviewerRole), requester = normalize(requesterRole);
        if (requester.equals("HR") || requester.equals("MANAGER")) return reviewer.equals("ADMIN");
        if (!requester.equals("EMPLOYEE")) return false;
        return reviewer.equals("ADMIN") || reviewer.equals("HR") || (reviewer.equals("MANAGER") && directReport);
    }
    private static String normalize(String role) { return role == null ? "" : role.trim().toUpperCase(Locale.ROOT).replaceFirst("^ROLE_", ""); }
}
