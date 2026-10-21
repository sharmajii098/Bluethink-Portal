package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.entity.Notification;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.repository.NotificationRepository;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;
    private  final EmployeeRepository employeeRepository;
    private final NotificationSseService notificationSseService;

    @Override
    public void createNotification(Long employeeId, String message){
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();

        Notification notification = new Notification();

        notification.setEmployee(employee);
        if (employee.getRegisterEmployee() != null) {
            notification.setRecipientUserId(employee.getRegisterEmployee().getUserId());
        }
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);
        notificationSseService.publish(saved);

    }



    public List<Notification>getEmployeeNotification (Long employeeId){
        return notificationRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

}
