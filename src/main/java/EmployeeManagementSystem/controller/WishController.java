package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.RegisterEmployee;
import EmployeeManagementSystem.entity.Wish;
import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.entity.Notification;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.repository.NotificationRepository;
import EmployeeManagementSystem.service.NotificationSseService;
import java.time.LocalDateTime;
import EmployeeManagementSystem.repository.RegisterEmployeeRepository;
import EmployeeManagementSystem.repository.WishRepository;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wish")
public class WishController {
    private final WishRepository wishRepository;
    private final RegisterEmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final EmployeeRepository employeeRecords;
    private final NotificationRepository notificationRepository;
    private final NotificationSseService notificationSseService;

    private RegisterEmployee currentEmployee(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        try {
            String username = jwtUtil.extractUsername(authorization.substring(7));
            RegisterEmployee employee = employeeRepository.findByUserId(username);
            if (employee == null) employee = employeeRepository.findByEmailIgnoreCase(username).orElse(null);
            if (employee == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Employee account not found.");
            return employee;
        } catch (ResponseStatusException ex) { throw ex; }
        catch (Exception ex) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session."); }
    }

    @GetMapping("/received")
    public List<Map<String, Object>> received(@RequestHeader("Authorization") String authorization) {
        RegisterEmployee recipient = currentEmployee(authorization);
        return wishRepository.findByReceiverUserIdOrderByCreatedAtDesc(recipient.getUserId(), PageRequest.of(0, 50)).stream().map(wish -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", wish.getId()); row.put("message", wish.getMessage());
            row.put("type", wish.getWishType()); row.put("createdAt", wish.getCreatedAt());
            RegisterEmployee sender = employeeRepository.findByUserId(wish.getSenderUserId());
            row.put("senderName", sender == null ? wish.getSenderUserId() : sender.getName());
            return row;
        }).toList();
    }


    /** Wishes authored by the current user, used to render reminders as already handled. */
    @GetMapping("/sent")
    public List<Map<String, Object>> sent(@RequestHeader("Authorization") String authorization) {
        RegisterEmployee sender = currentEmployee(authorization);
        return wishRepository.findBySenderUserIdOrderByCreatedAtDesc(sender.getUserId(), PageRequest.of(0, 200)).stream().map(wish -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("receiverId", wish.getReceiverUserId()); row.put("type", wish.getWishType());
            row.put("message", wish.getMessage()); row.put("createdAt", wish.getCreatedAt());
            return row;
        }).toList();
    }

    @PostMapping("/send")
    @Transactional
    public ResponseEntity<?> sendWish(@RequestHeader("Authorization") String authorization,
                                      @RequestBody Map<String, String> requestData) {
        RegisterEmployee sender = currentEmployee(authorization);
        if (requestData == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A wish payload is required.");
        String receiverId = java.util.Optional.ofNullable(requestData.get("receiverId")).orElse("").trim();
        String type = java.util.Optional.ofNullable(requestData.get("type")).orElse("").trim().toUpperCase(java.util.Locale.ROOT);
        String message = java.util.Optional.ofNullable(requestData.get("message")).orElse("").trim();
        if (receiverId.isBlank() || message.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recipient and message are required.");
        if (receiverId.length() > 120) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid recipient.");
        if (message.codePoints().filter(ch -> ch == '\n').count() > 20) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please keep the message to 20 lines or fewer.");
        if (!type.equals("BIRTHDAY") && !type.equals("WORK_ANNIVERSARY") && !type.equals("ANNIVERSARY")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported celebration type.");
        }
        if (message.length() > 500) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wish must be 500 characters or fewer.");
        RegisterEmployee recipient = employeeRepository.findByUserId(receiverId);
        if (recipient == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient employee not found.");
        if (sender.getUserId().equals(recipient.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send a wish to yourself.");
        }
        // One wish per sender/recipient/celebration type per calendar year. The unique
        // database key makes the operation idempotent even when the user double-clicks.
        String wishKey = sender.getUserId() + "|" + recipient.getUserId() + "|" + type + "|" + java.time.LocalDate.now().getYear();
        if (wishRepository.existsByWishKey(wishKey)) {
            return ResponseEntity.ok(Map.of("message", "You have already sent wishes to this colleague for this celebration this year.", "alreadySent", true));
        }
        Wish wish = new Wish();
        wish.setSenderUserId(sender.getUserId()); wish.setReceiverUserId(recipient.getUserId());
        wish.setWishType(type); wish.setMessage(message); wish.setWishKey(wishKey);
        wishRepository.saveAndFlush(wish);

        // Persist a notification in the recipient's inbox so the person being wished is informed.
        Employee recipientRecord = employeeRecords.findByRegisterEmployee_UserId(recipient.getUserId())
                .or(() -> employeeRecords.findByEmployeeProfile_UserId(recipient.getUserId()))
                .orElse(null);
        Notification notification = new Notification();
        notification.setEmployee(recipientRecord);
        notification.setRecipientUserId(recipient.getUserId());
        String kind = type.equals("BIRTHDAY") ? "birthday" : "work anniversary";
        notification.setMessage("🎉 " + sender.getName() + " sent you " + kind + " wishes: " + message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        Notification savedNotification = notificationRepository.save(notification);
        notificationSseService.publish(savedNotification);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Wish sent successfully. The recipient has been notified.", "alreadySent", false));
    }
}
