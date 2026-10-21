package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.AttendanceRegularization;
import java.util.List;
import java.util.Map;
public interface AttendanceRegularizationService {
 List<AttendanceRegularization> all(); List<AttendanceRegularization> my(String userId);
 AttendanceRegularization create(AttendanceRegularization x,String userId,String name);
 AttendanceRegularization review(Long id, Map<String,Object> body,String reviewerUserId,String reviewerName);
 AttendanceRegularization applyLegacyApproved(Long id);
}
