package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.TalentSuccessionPlan;
import java.util.List;

public interface TalentSuccessionPlanService {
    List<TalentSuccessionPlan> all();
    List<TalentSuccessionPlan> mine(String employeeUserId);
    TalentSuccessionPlan create(TalentSuccessionPlan input,String actorUserId);
    TalentSuccessionPlan update(Long id,TalentSuccessionPlan input,String actorUserId);
    TalentSuccessionPlan review(Long id,String status,String comment,String actorUserId);
    void delete(Long id);
}
