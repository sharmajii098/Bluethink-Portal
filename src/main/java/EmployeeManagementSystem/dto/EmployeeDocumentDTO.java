package EmployeeManagementSystem.dto;
import java.time.*;
public record EmployeeDocumentDTO(Long id,String employeeId,String employeeName,String category,String originalFilename,String contentType,Long fileSize,String status,LocalDate issueDate,LocalDate expiryDate,String description,String uploadedBy,LocalDateTime uploadedAt,String verifiedBy,LocalDateTime verifiedAt,String reviewComment,boolean expiringSoon,boolean expired){ }
