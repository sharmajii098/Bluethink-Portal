package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.Announcement;
import java.util.List;
import java.util.Map;
public interface AnnouncementService {
 List<Map<String,Object>> listForUser(String userId,String role);
 List<Announcement> listAll();
 Announcement create(Announcement a,String userId);
 Announcement update(Long id,Announcement a);
 void delete(Long id);
 Map<String,Object> markRead(Long id,String userId,boolean acknowledge);
}
