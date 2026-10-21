package EmployeeManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private boolean success;
    private String message;
    private String token;
    private String userId;
    private String role;
    private String fullName;
    private String designation;
    private String workMode;
    private List<String> permissions;
}
