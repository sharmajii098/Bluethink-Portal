package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.TravelRequest;
import java.util.List;
public interface TravelRequestService {
 List<TravelRequest> my(String userId); List<TravelRequest> all(); TravelRequest create(TravelRequest x,String userId,String name);
 TravelRequest update(Long id,TravelRequest x,String userId); TravelRequest review(Long id,String status,String comment,String reviewer); void delete(Long id,String userId); TravelRequest get(Long id);
}
