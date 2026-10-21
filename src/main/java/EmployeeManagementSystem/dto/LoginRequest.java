package EmployeeManagementSystem.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String password;
    private String workMode;
    private Double latitude;
    private Double longitude;
    private Double accuracy;
}
