package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.ProbationReview;
import java.util.*;
public interface ProbationReviewService { List<ProbationReview> all(); List<ProbationReview> my(String userId); ProbationReview create(ProbationReview x,String userId,String name); ProbationReview review(Long id, String status, String decision, Integer rating, String comment, String reviewer); }
