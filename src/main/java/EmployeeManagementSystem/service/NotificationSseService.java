package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Keeps the authenticated user's browser connected to the notification stream.
 * SSE is server -> browser only, which is a good fit for HRMS notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationSseService {

    private static final long SSE_TIMEOUT = 0L; // keep the connection open

    private final ObjectMapper objectMapper;
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId) {
        String uid = userId == null ? "" : userId.trim();
        if (uid.isBlank()) {
            throw new IllegalArgumentException("Notification user id is required");
        }

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        CopyOnWriteArrayList<SseEmitter> userEmitters =
                emitters.computeIfAbsent(uid, ignored -> new CopyOnWriteArrayList<>());
        userEmitters.add(emitter);

        Runnable cleanup = () -> remove(uid, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(error -> cleanup.run());

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("connected", true)));
        } catch (IOException ex) {
            cleanup.run();
            emitter.completeWithError(ex);
        }

        return emitter;
    }

    public void publish(Notification notification) {
        if (notification == null) return;

        String uid = notification.getRecipientUserId();
        if ((uid == null || uid.isBlank()) && notification.getEmployee() != null
                && notification.getEmployee().getRegisterEmployee() != null) {
            uid = notification.getEmployee().getRegisterEmployee().getUserId();
        }
        if (uid == null || uid.isBlank()) return;

        Map<String, Object> payload = Map.of(
                "id", notification.getId(),
                "message", notification.getMessage() == null ? "Notification" : notification.getMessage(),
                "createdAt", notification.getCreatedAt() == null
                        ? ""
                        : notification.getCreatedAt().atOffset(ZoneOffset.UTC).toString(),
                "read", notification.isRead()
        );

        final String recipientUid = uid;
        List<SseEmitter> current = emitters.get(recipientUid);
        if (current == null || current.isEmpty()) return;

        List<SseEmitter> failed = new ArrayList<>();
        for (SseEmitter emitter : current) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .id(String.valueOf(notification.getId()))
                        .data(objectMapper.writeValueAsString(payload)));
            } catch (Exception ex) {
                failed.add(emitter);
            }
        }
        failed.forEach(emitter -> remove(recipientUid, emitter));
    }

    public void publishHeartbeat() {
        emitters.forEach((uid, current) -> {
            for (SseEmitter emitter : current) {
                try {
                    emitter.send(SseEmitter.event().name("heartbeat").data("ok"));
                } catch (Exception ex) {
                    remove(uid, emitter);
                }
            }
        });
    }

    private void remove(String uid, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> current = emitters.get(uid);
        if (current == null) return;
        current.remove(emitter);
        if (current.isEmpty()) emitters.remove(uid, current);
    }
}
