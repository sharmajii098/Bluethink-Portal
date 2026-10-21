package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.ShiftSwapRequest;
import java.util.List;
import java.util.Map;

public interface ShiftSwapRequestService {
    List<ShiftSwapRequest> my(String userId);
    List<ShiftSwapRequest> all();
    ShiftSwapRequest create(ShiftSwapRequest request, String userId, String name);
    ShiftSwapRequest review(Long id, Map<String,Object> body, String reviewerUserId);
    ShiftSwapRequest consent(Long id, String targetUserId, boolean accepted, String comment);
    List<Map<String,Object>> eligible(String requesterUserId, java.time.LocalDate requesterDate, java.time.LocalDate targetDate);
    Map<String,Object> preview(String requesterUserId, String targetUserId, java.time.LocalDate requesterDate, java.time.LocalDate targetDate);
}
