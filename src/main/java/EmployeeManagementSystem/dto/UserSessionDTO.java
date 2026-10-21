package EmployeeManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserSessionDTO {
    private Long id;
    private String employeeId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime logoutAt;
    private boolean current;
    private boolean active;
}
