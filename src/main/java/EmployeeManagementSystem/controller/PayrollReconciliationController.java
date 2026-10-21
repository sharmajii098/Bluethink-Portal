package EmployeeManagementSystem.controller;
import EmployeeManagementSystem.service.PayrollReconciliationService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/payroll-reconciliations")
public class PayrollReconciliationController {
 private final PayrollReconciliationService service; private final EmployeeManagementSystem.service.MonthlySalaryService salaries;
 public PayrollReconciliationController(PayrollReconciliationService service,EmployeeManagementSystem.service.MonthlySalaryService salaries){this.service=service;this.salaries=salaries;}
 @GetMapping("/employees") public List<PayrollReconciliationService.EmployeeOption> employees(){return service.employees();}
 @PostMapping("/{id}/generate") public EmployeeManagementSystem.entity.Salary generate(@PathVariable Long id){var r=service.get(id);var period=java.time.YearMonth.parse(r.period());return salaries.generateMonthlySalary(r.profileId(),period.getMonth().name(),period.getYear());}
 @GetMapping public List<PayrollReconciliationService.View> list(@RequestParam String period){return service.list(period);}
 @PostMapping("/preview") public PayrollReconciliationService.Report preview(@RequestBody PayrollReconciliationService.Input input){return service.preview(input);}
 @PostMapping public PayrollReconciliationService.View prepare(@RequestBody PayrollReconciliationService.Input input){return service.prepare(input);}
 @PostMapping("/{id}/approve") public PayrollReconciliationService.View approve(@PathVariable Long id){return service.approve(id);}
}
