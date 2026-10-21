package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.service.ApprovedPayrollPolicyService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/payroll-policies")
public class ApprovedPayrollPolicyController {
 private final ApprovedPayrollPolicyService service;
 public ApprovedPayrollPolicyController(ApprovedPayrollPolicyService service){this.service=service;}
 @GetMapping public List<ApprovedPayrollPolicyService.View> list(){return service.list();}
 @PostMapping public ApprovedPayrollPolicyService.View create(@RequestBody ApprovedPayrollPolicyService.Create input){return service.create(input);}
 @PostMapping("/{id}/approve") public ApprovedPayrollPolicyService.View approve(@PathVariable Long id){return service.approve(id);}
}
