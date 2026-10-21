package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.CareerMovementRequest; import java.util.*;
public interface CareerMovementService { List<CareerMovementRequest> my(String userId); List<CareerMovementRequest> all(); CareerMovementRequest create(CareerMovementRequest x,String userId,String name); CareerMovementRequest review(Long id,String status,String comment,String reviewer); }
