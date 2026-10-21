package EmployeeManagementSystem.controller;
import EmployeeManagementSystem.entity.HrLetterTemplate;
import EmployeeManagementSystem.service.HrLetterTemplateService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/hr-letter-templates")
public class HrLetterTemplateController {
 private final HrLetterTemplateService service;
 public HrLetterTemplateController(HrLetterTemplateService service){this.service=service;}
 @GetMapping public List<HrLetterTemplate> list(){return service.list();}
 @PostMapping public HrLetterTemplate create(@RequestBody HrLetterTemplateService.Input input){return service.create(input);}
 @PutMapping("/{id}") public HrLetterTemplate update(@PathVariable Long id,@RequestBody HrLetterTemplateService.Input input){return service.update(id,input);}
 @GetMapping("/starters") public java.util.Map<String,String> starters(){return service.starters();}
 @PostMapping("/{id}/approve") public HrLetterTemplate approve(@PathVariable Long id){return service.approve(id);}
}
