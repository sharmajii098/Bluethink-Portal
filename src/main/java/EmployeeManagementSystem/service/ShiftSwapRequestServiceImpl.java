package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
public class ShiftSwapRequestServiceImpl implements ShiftSwapRequestService {
    private static final List<String> ACTIVE = List.of("SUBMITTED", "UNDER_REVIEW", "APPROVED");

    private final ShiftSwapRequestRepository repo;
    private final ShiftAssignmentRepository assignments;
    private final EmployeeRepository employees;
    private final NotificationService notifications;
    private final RegisterEmployeeRepository accounts;

    public ShiftSwapRequestServiceImpl(ShiftSwapRequestRepository repo, ShiftAssignmentRepository assignments,
                                       EmployeeRepository employees, NotificationService notifications,
                                       RegisterEmployeeRepository accounts) {
        this.repo = repo;
        this.assignments = assignments;
        this.employees = employees;
        this.notifications = notifications;
        this.accounts = accounts;
    }

    @Transactional(readOnly = true)
    public List<ShiftSwapRequest> my(String user) {
        return repo.findByRequesterUserIdOrTargetUserIdOrderByCreatedAtDesc(user, user);
    }

    @Transactional(readOnly = true)
    public List<ShiftSwapRequest> all() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    private Employee requireEmployee(String user) {
        return employees.findByRegisterEmployee_UserId(user)
                .or(() -> employees.findByEmployeeProfile_UserId(user))
                .orElseThrow(() -> new IllegalArgumentException("Employee account not found."));
    }

    private String userId(ShiftAssignment assignment) {
        if (assignment == null || assignment.getEmployee() == null) return null;
        Employee employee = assignment.getEmployee();
        if (employee.getRegisterEmployee() != null && employee.getRegisterEmployee().getUserId() != null)
            return employee.getRegisterEmployee().getUserId();
        if (employee.getEmployeeProfile() != null) return employee.getEmployeeProfile().getUserId();
        return null;
    }

    private ShiftAssignment assigned(String user, LocalDate date) {
        return assignments.findByEmployee_RegisterEmployee_UserIdAndShiftDate(user, date)
                .or(() -> assignments.findByEmployee_EmployeeProfile_UserIdAndShiftDate(user, date))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No scheduled shift is assigned to " + user + " on " + date + ". Ask HR/Manager to assign the shift first."));
    }

    private void validDate(LocalDate date) {
        if (date == null || date.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Choose today or a future shift date.");
    }

    private void ensureScheduled(ShiftAssignment assignment) {
        if (assignment == null || assignment.getShift() == null)
            throw new IllegalArgumentException("Shift assignment is incomplete.");
        if (!"SCHEDULED".equalsIgnoreCase(assignment.getStatus()))
            throw new IllegalArgumentException("Only scheduled, uncompleted shifts can be exchanged.");
        if (!assignment.getShiftDate().isAfter(LocalDate.now()) &&
                !LocalTime.now().isBefore(assignment.getShift().getStartTime()))
            throw new IllegalArgumentException("A shift that has already started cannot be exchanged.");
    }

    private void ensureAvailable(String user, LocalDate date) {
        if (repo.countActiveForShift(user, date, ACTIVE) > 0)
            throw new IllegalArgumentException("Another active shift swap already includes " + user + " on " + date + ".");
    }

    private void ensureDestinationFree(ShiftAssignment requesterAssignment, ShiftAssignment targetAssignment) {
        if (requesterAssignment.getShiftDate().equals(targetAssignment.getShiftDate())) return;

        Employee requesterEmployee = requesterAssignment.getEmployee();
        Employee targetEmployee = targetAssignment.getEmployee();
        LocalDate requesterDate = requesterAssignment.getShiftDate();
        LocalDate targetDate = targetAssignment.getShiftDate();

        Optional<ShiftAssignment> requesterOnTargetDate =
                assignments.findByEmployee_IdAndShiftDate(requesterEmployee.getId(), targetDate);
        if (requesterOnTargetDate.isPresent() && !requesterOnTargetDate.get().getId().equals(targetAssignment.getId())) {
            throw new IllegalArgumentException(displayName(requesterEmployee) +
                    " already has another shift on " + targetDate + ". Choose another date.");
        }

        Optional<ShiftAssignment> targetOnRequesterDate =
                assignments.findByEmployee_IdAndShiftDate(targetEmployee.getId(), requesterDate);
        if (targetOnRequesterDate.isPresent() && !targetOnRequesterDate.get().getId().equals(requesterAssignment.getId())) {
            throw new IllegalArgumentException(displayName(targetEmployee) +
                    " already has another shift on " + requesterDate + ". Choose another date.");
        }
    }

    private void ensureMeaningfulSwap(ShiftAssignment requesterAssignment, ShiftAssignment targetAssignment) {
        if (requesterAssignment.getShiftDate().equals(targetAssignment.getShiftDate()) &&
                Objects.equals(requesterAssignment.getShift().getId(), targetAssignment.getShift().getId())) {
            throw new IllegalArgumentException("Both employees already have the same shift on this date. There is nothing to swap.");
        }
    }

    private String displayName(Employee employee) {
        if (employee == null) return "Employee";
        if (employee.getFullName() != null && !employee.getFullName().isBlank()) return employee.getFullName();
        if (employee.getRegisterEmployee() != null && employee.getRegisterEmployee().getUserId() != null)
            return employee.getRegisterEmployee().getUserId();
        return "Employee";
    }

    private Map<String, Object> shiftDetails(ShiftAssignment assignment) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("assignmentId", assignment.getId());
        out.put("userId", userId(assignment));
        out.put("employeeName", displayName(assignment.getEmployee()));
        out.put("date", assignment.getShiftDate());
        out.put("shiftId", assignment.getShift().getId());
        out.put("shiftName", assignment.getShift().getName());
        out.put("startTime", assignment.getShift().getStartTime());
        out.put("endTime", assignment.getShift().getEndTime());
        out.put("status", assignment.getStatus());
        return out;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> eligible(String requester, LocalDate ownDate, LocalDate targetDate) {
        validDate(ownDate);
        validDate(targetDate);

        ShiftAssignment ownAssignment = assigned(requester, ownDate);
        ensureScheduled(ownAssignment);
        ensureAvailable(requester, ownDate);

        List<Map<String, Object>> out = new ArrayList<>();
        for (ShiftAssignment candidate : assignments.findByShiftDateBetweenOrderByShiftDateAsc(targetDate, targetDate)) {
            String candidateUser = userId(candidate);
            if (candidateUser == null || requester.equalsIgnoreCase(candidateUser)) continue;
            try {
                ensureScheduled(candidate);
                ensureAvailable(candidateUser, targetDate);
                ensureDestinationFree(ownAssignment, candidate);
                ensureMeaningfulSwap(ownAssignment, candidate);
                out.add(shiftDetails(candidate));
            } catch (IllegalArgumentException ignored) {
                // The employee remains hidden from the dropdown when the requested exchange is not safe.
            }
        }
        out.sort(Comparator.comparing(x -> String.valueOf(x.get("employeeName")), String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> preview(String requester, String target, LocalDate ownDate, LocalDate targetDate) {
        validDate(ownDate);
        validDate(targetDate);
        if (target == null || target.isBlank() || requester.equalsIgnoreCase(target))
            throw new IllegalArgumentException("Choose another employee.");

        ShiftAssignment requesterAssignment = assigned(requester, ownDate);
        ShiftAssignment targetAssignment = assigned(target, targetDate);
        ensureScheduled(requesterAssignment);
        ensureScheduled(targetAssignment);
        ensureAvailable(requester, ownDate);
        ensureAvailable(target, targetDate);
        ensureDestinationFree(requesterAssignment, targetAssignment);
        ensureMeaningfulSwap(requesterAssignment, targetAssignment);

        Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("requester", shiftDetails(requesterAssignment));
        preview.put("target", shiftDetails(targetAssignment));
        preview.put("sameDate", ownDate.equals(targetDate));
        preview.put("swapMode", ownDate.equals(targetDate) ? "SHIFT_TIMES" : "ASSIGNMENTS");
        return preview;
    }

    private void notifyEmployee(String user, String message) {
        Employee employee = requireEmployee(user);
        notifications.createNotification(employee.getId(), message);
    }

    private ShiftAssignment[] lockPair(ShiftAssignment requesterAssignment, ShiftAssignment targetAssignment) {
        ShiftAssignment first = assignments.lockAssignment(Math.min(requesterAssignment.getId(), targetAssignment.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Scheduled shift no longer exists."));
        ShiftAssignment second = assignments.lockAssignment(Math.max(requesterAssignment.getId(), targetAssignment.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Scheduled shift no longer exists."));
        if (requesterAssignment.getId().equals(first.getId())) return new ShiftAssignment[]{first, second};
        return new ShiftAssignment[]{second, first};
    }

    private ShiftAssignment[] currentAssignmentsForRequest(ShiftSwapRequest request, boolean lock) {
        ShiftAssignment requesterAssignment;
        ShiftAssignment targetAssignment;

        if (request.getRequesterAssignmentId() != null && request.getTargetAssignmentId() != null) {
            if (lock) {
                Long requesterId = request.getRequesterAssignmentId();
                Long targetId = request.getTargetAssignmentId();
                ShiftAssignment first = assignments.lockAssignment(Math.min(requesterId, targetId))
                        .orElseThrow(() -> new IllegalArgumentException("Scheduled shift no longer exists."));
                ShiftAssignment second = assignments.lockAssignment(Math.max(requesterId, targetId))
                        .orElseThrow(() -> new IllegalArgumentException("Scheduled shift no longer exists."));
                if (requesterId.equals(first.getId())) {
                    requesterAssignment = first;
                    targetAssignment = second;
                } else {
                    requesterAssignment = second;
                    targetAssignment = first;
                }
            } else {
                requesterAssignment = assignments.findById(request.getRequesterAssignmentId())
                        .orElseThrow(() -> new IllegalArgumentException("Requester shift no longer exists."));
                targetAssignment = assignments.findById(request.getTargetAssignmentId())
                        .orElseThrow(() -> new IllegalArgumentException("Target shift no longer exists."));
            }
        } else {
            // Backward compatibility for requests created before assignment snapshots were added.
            ShiftAssignment requesterFound = assigned(request.getRequesterUserId(), request.getRequesterDate());
            ShiftAssignment targetFound = assigned(request.getTargetUserId(), request.getTargetDate());
            if (lock) {
                ShiftAssignment[] pair = lockPair(requesterFound, targetFound);
                requesterAssignment = pair[0];
                targetAssignment = pair[1];
            } else {
                requesterAssignment = requesterFound;
                targetAssignment = targetFound;
            }
        }

        ShiftSwapRules.requireAssignmentSnapshot(
                request.getRequesterAssignmentId(), requesterAssignment.getId(),
                request.getRequesterUserId(), userId(requesterAssignment),
                request.getRequesterDate(), requesterAssignment.getShiftDate(),
                request.getRequesterShiftId(), requesterAssignment.getShift().getId());
        ShiftSwapRules.requireAssignmentSnapshot(
                request.getTargetAssignmentId(), targetAssignment.getId(),
                request.getTargetUserId(), userId(targetAssignment),
                request.getTargetDate(), targetAssignment.getShiftDate(),
                request.getTargetShiftId(), targetAssignment.getShift().getId());

        return new ShiftAssignment[]{requesterAssignment, targetAssignment};
    }

    @Transactional
    public ShiftSwapRequest create(ShiftSwapRequest incoming, String requester, String ignoredName) {
        if (incoming == null) throw new IllegalArgumentException("Shift swap details required.");
        String target = incoming.getTargetUserId();
        if (target == null || target.isBlank() || target.equalsIgnoreCase(requester))
            throw new IllegalArgumentException("Choose a different employee.");

        LocalDate ownDate = incoming.getRequesterDate();
        LocalDate targetDate = incoming.getTargetDate();
        validDate(ownDate);
        validDate(targetDate);

        Employee requesterEmployee = requireEmployee(requester);
        Employee targetEmployee = requireEmployee(target);
        ShiftAssignment requesterFound = assigned(requester, ownDate);
        ShiftAssignment targetFound = assigned(target, targetDate);
        ShiftAssignment[] locked = lockPair(requesterFound, targetFound);
        ShiftAssignment requesterAssignment = locked[0];
        ShiftAssignment targetAssignment = locked[1];

        ensureScheduled(requesterAssignment);
        ensureScheduled(targetAssignment);
        ensureAvailable(requester, ownDate);
        ensureAvailable(target, targetDate);
        ensureDestinationFree(requesterAssignment, targetAssignment);
        ensureMeaningfulSwap(requesterAssignment, targetAssignment);

        ShiftSwapRequest request = new ShiftSwapRequest();
        request.setRequestNumber("SWP-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase(Locale.ROOT));
        request.setRequesterUserId(requester);
        request.setRequesterName(displayName(requesterEmployee));
        request.setTargetUserId(target);
        request.setTargetName(displayName(targetEmployee));
        request.setRequesterDate(ownDate);
        request.setTargetDate(targetDate);
        request.setRequesterAssignmentId(requesterAssignment.getId());
        request.setTargetAssignmentId(targetAssignment.getId());
        request.setRequesterShift(requesterAssignment.getShift().getName());
        request.setTargetShift(targetAssignment.getShift().getName());
        request.setRequesterShiftId(requesterAssignment.getShift().getId());
        request.setTargetShiftId(targetAssignment.getShift().getId());
        request.setReason(incoming.getReason() == null ? "" : incoming.getReason().trim());
        if (request.getReason().isBlank()) throw new IllegalArgumentException("Provide a reason for the exchange.");
        request.setTargetConsentStatus("PENDING");
        request.setStatus("SUBMITTED");

        ShiftSwapRequest saved = repo.save(request);
        notifyEmployee(target, request.getRequesterName() + " requested a shift swap. Please accept or decline request " + request.getRequestNumber() + ".");
        return saved;
    }

    @Transactional
    public ShiftSwapRequest consent(Long id, String targetUser, boolean accept, String comment) {
        ShiftSwapRequest request = repo.lockById(id)
                .orElseThrow(() -> new NoSuchElementException("Shift swap request not found."));
        if (!targetUser.equals(request.getTargetUserId()))
            throw new SecurityException("Only the selected employee can respond.");
        if (!List.of("SUBMITTED", "UNDER_REVIEW").contains(request.getStatus()) ||
                !"PENDING".equalsIgnoreCase(String.valueOf(request.getTargetConsentStatus())))
            throw new IllegalArgumentException("This request is no longer awaiting your response.");

        if (!accept && (comment == null || comment.trim().isBlank()))
            throw new IllegalArgumentException("Please provide a reason for declining the shift swap.");

        if (accept) {
            ShiftAssignment[] pair = currentAssignmentsForRequest(request, false);
            ensureScheduled(pair[0]);
            ensureScheduled(pair[1]);
            ensureDestinationFree(pair[0], pair[1]);
            ensureMeaningfulSwap(pair[0], pair[1]);
        }

        request.setTargetConsentStatus(accept ? "ACCEPTED" : "DECLINED");
        request.setTargetConsentComment(comment == null ? "" : comment.trim());
        request.setTargetRespondedAt(LocalDateTime.now());
        if (!accept) request.setStatus("REJECTED");

        ShiftSwapRequest saved = repo.save(request);
        notifyEmployee(request.getRequesterUserId(), request.getTargetName() +
                (accept ? " accepted your shift swap request. Awaiting Manager/HR approval."
                        : " declined your shift swap request."));
        if (accept) notifyApprovers(request);
        return saved;
    }

    private void notifyApprovers(ShiftSwapRequest request) {
        Set<Long> notified = new HashSet<>();
        Employee requester = requireEmployee(request.getRequesterUserId());
        Employee manager = requester.getReportingManager();
        if (manager != null && manager.getId() != null && notified.add(manager.getId())) {
            notifications.createNotification(manager.getId(), "Shift swap " + request.getRequestNumber() +
                    " is ready for your review after employee consent.");
        }
        for (RegisterEmployee account : accounts.findAll()) {
            if (account.getRole() != null && "HR".equals(account.getRole().replace("ROLE_", "").toUpperCase(Locale.ROOT))) {
                employees.findByRegisterEmployee_UserId(account.getUserId()).ifPresent(hr -> {
                    if (notified.add(hr.getId())) {
                        notifications.createNotification(hr.getId(), "Shift swap " + request.getRequestNumber() +
                                " is awaiting Manager/HR approval.");
                    }
                });
            }
        }
    }

    @Transactional
    public ShiftSwapRequest review(Long id, Map<String, Object> body, String reviewer) {
        ShiftSwapRequest request = repo.lockById(id)
                .orElseThrow(() -> new NoSuchElementException("Shift swap request not found."));
        ShiftSwapRules.requirePending(request.getStatus());

        String status = String.valueOf(body.get("status")).toUpperCase(Locale.ROOT);
        if (!Set.of("APPROVED", "REJECTED", "UNDER_REVIEW").contains(status))
            throw new IllegalArgumentException("Invalid review status.");
        if ("REJECTED".equals(status) &&
                (body.get("comment") == null || String.valueOf(body.get("comment")).isBlank()))
            throw new IllegalArgumentException("A rejection reason is required.");

        if ("APPROVED".equals(status)) {
            ShiftSwapRules.requireConsent(request.getTargetConsentStatus());
            validDate(request.getRequesterDate());
            validDate(request.getTargetDate());

            ShiftAssignment[] pair = currentAssignmentsForRequest(request, true);
            ShiftAssignment requesterAssignment = pair[0];
            ShiftAssignment targetAssignment = pair[1];
            ensureScheduled(requesterAssignment);
            ensureScheduled(targetAssignment);
            ensureDestinationFree(requesterAssignment, targetAssignment);
            ensureMeaningfulSwap(requesterAssignment, targetAssignment);

            if (requesterAssignment.getShiftDate().equals(targetAssignment.getShiftDate())) {
                // Same-day exchange: employees remain on the date and exchange shift timings.
                Shift requesterShift = requesterAssignment.getShift();
                requesterAssignment.setShift(targetAssignment.getShift());
                targetAssignment.setShift(requesterShift);
            } else {
                // Cross-date exchange: employees exchange the two scheduled assignment slots.
                Employee requesterEmployee = requesterAssignment.getEmployee();
                requesterAssignment.setEmployee(targetAssignment.getEmployee());
                targetAssignment.setEmployee(requesterEmployee);
            }

            requesterAssignment.setSwapStatus("SWAPPED");
            targetAssignment.setSwapStatus("SWAPPED");
            assignments.save(requesterAssignment);
            assignments.save(targetAssignment);
        }

        request.setStatus(status);
        request.setReviewerUserId(reviewer);
        request.setReviewerComment(body.get("comment") == null ? "" : String.valueOf(body.get("comment")).trim());
        request.setReviewedAt(LocalDateTime.now());
        ShiftSwapRequest saved = repo.save(request);

        if (!"UNDER_REVIEW".equals(status)) {
            String message = "Shift swap " + request.getRequestNumber() + " was " + status.toLowerCase(Locale.ROOT) + ".";
            notifyEmployee(request.getRequesterUserId(), message);
            notifyEmployee(request.getTargetUserId(), message);
        }
        return saved;
    }
}
