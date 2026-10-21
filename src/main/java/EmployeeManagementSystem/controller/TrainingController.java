package EmployeeManagementSystem.controller;
import EmployeeManagementSystem.entity.TrainingCourse; import EmployeeManagementSystem.security.jwt.JwtUtil; import EmployeeManagementSystem.service.TrainingService; import EmployeeManagementSystem.repository.EmployeeRepository; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException; import java.util.*;
@RestController @RequestMapping("/api/training")
public class TrainingController{
 private final TrainingService service; private final JwtUtil jwt; private final EmployeeRepository employees;
 public TrainingController(TrainingService s,JwtUtil j,EmployeeRepository e){service=s;jwt=j;employees=e;}
 private String tok(String a){if(a==null||!a.startsWith("Bearer "))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required.");return a.substring(7);} private String user(String a){return jwt.extractUsername(tok(a));} private String role(String a){String r=jwt.extractRole(tok(a));return r==null?"":r.replace("ROLE_","").toUpperCase();} private void mgmt(String r){if(!"ADMIN".equals(r)&&!"HR".equals(r))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");}
 @GetMapping public List<Map<String,Object>> catalog(@RequestHeader("Authorization")String a){return service.catalog(user(a));}
 @GetMapping("/my") public List<Map<String,Object>> my(@RequestHeader("Authorization")String a){return service.my(user(a));}
 @GetMapping("/all") public List<TrainingCourse> all(@RequestHeader("Authorization")String a){mgmt(role(a));return service.all();}
 @GetMapping("/compliance") public List<Map<String,Object>> compliance(@RequestHeader("Authorization")String a){mgmt(role(a));return service.compliance();}
 @PostMapping public TrainingCourse create(@RequestHeader("Authorization")String a,@RequestBody TrainingCourse c){mgmt(role(a));c.setId(null);return service.save(c);}
 @PutMapping("/{id}") public TrainingCourse update(@PathVariable Long id,@RequestHeader("Authorization")String a,@RequestBody TrainingCourse c){mgmt(role(a));c.setId(id);return service.save(c);}
 @DeleteMapping("/{id}") public Map<String,Object> delete(@PathVariable Long id,@RequestHeader("Authorization")String a){mgmt(role(a));service.delete(id);return Map.of("success",true);}
 @PostMapping("/{id}/enroll") public Map<String,Object> enroll(@PathVariable Long id,@RequestHeader("Authorization")String a){String u=user(a);String n=employees.findByRegisterEmployee_UserId(u).map(e->e.getFullName()).orElse(u);try{return service.enroll(id,u,n);}catch(IllegalArgumentException x){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,x.getMessage());}}
 @PutMapping("/{id}/progress") public Map<String,Object> progress(@PathVariable Long id,@RequestHeader("Authorization")String a,@RequestBody Map<String,Object> b){try{return service.progress(id,user(a),Integer.parseInt(String.valueOf(b.get("progressPercent"))));}catch(NoSuchElementException x){throw new ResponseStatusException(HttpStatus.NOT_FOUND,x.getMessage());}catch(Exception x){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,x.getMessage());}}
 @PutMapping("/{id}/feedback") public Map<String,Object> feedback(@PathVariable Long id,@RequestHeader("Authorization")String a,@RequestBody Map<String,Object> b){return service.feedback(id,user(a),String.valueOf(b.getOrDefault("feedback","")));}
}
