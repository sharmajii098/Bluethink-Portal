package EmployeeManagementSystem.service;

import java.time.LocalDate;
import java.util.Objects;

/** Stateless guards used by shift-swap approval. */
public final class ShiftSwapRules {
    private ShiftSwapRules() {}

    public static void requirePending(String status) {
        if (!"SUBMITTED".equals(status) && !"UNDER_REVIEW".equals(status))
            throw new IllegalArgumentException("This request has already been finalized.");
    }

    public static void requireConsent(String consent) {
        if (!"ACCEPTED".equals(consent))
            throw new IllegalArgumentException("The other employee must accept before approval.");
    }

    public static void requireUnchanged(Long expectedA, Long actualA, Long expectedB, Long actualB) {
        if ((expectedA != null && !expectedA.equals(actualA)) ||
                (expectedB != null && !expectedB.equals(actualB)))
            throw new IllegalArgumentException("A shift assignment has changed. Submit a new request.");
    }

    public static void requireAssignmentSnapshot(Long expectedAssignmentId, Long actualAssignmentId,
                                                 String expectedUserId, String actualUserId,
                                                 LocalDate expectedDate, LocalDate actualDate,
                                                 Long expectedShiftId, Long actualShiftId) {
        if ((expectedAssignmentId != null && !Objects.equals(expectedAssignmentId, actualAssignmentId)) ||
                !Objects.equals(expectedUserId, actualUserId) ||
                !Objects.equals(expectedDate, actualDate) ||
                (expectedShiftId != null && !Objects.equals(expectedShiftId, actualShiftId))) {
            throw new IllegalArgumentException("A scheduled shift changed after this request was created. Submit a new shift swap request.");
        }
    }
}
