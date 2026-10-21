package EmployeeManagementSystem.controller;
import EmployeeManagementSystem.entity.AttendanceRegularization;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.service.AttendanceRegularizationService; import EmployeeManagementSystem.service.RequestScopeService;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController @RequestMapping("/api/attendance-regularizations")
public class AttendanceRegularizationController{
 private final AttendanceRegularizationService service; private final JwtUtil jwt; private final EmployeeRepository employees; private final RequestScopeService scope;
 public AttendanceRegularizationController(AttendanceRegularizationService s,JwtUtil j,EmployeeRepository e,RequestScopeService requestScope){service=s;jwt=j;employees=e;scope=requestScope;}
 private String token(String a){if(a==null||!a.startsWith("Bearer "))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required.");return a.substring(7);} private String user(String a){return jwt.extractUsername(token(a));} private String role(String a){String r=jwt.extractRole(token(a));return r==null?"":r.replace("ROLE_","").toUpperCase();} private boolean management(String r){return Set.of("ADMIN","HR","MANAGER").contains(r);}
 @GetMapping public List<AttendanceRegularization> all(@RequestHeader("Authorization") String a){String currentRole=role(a);if(!management(currentRole))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");return scope.visibleTo(currentRole,user(a),service.all());}
 @GetMapping("/my") public List<AttendanceRegularization> my(@RequestHeader("Authorization") String a){return service.my(user(a));}
 @PostMapping public AttendanceRegularization create(@RequestHeader("Authorization") String a,@RequestBody AttendanceRegularization x){String u=user(a);String n=employees.findByRegisterEmployee_UserId(u).map(e->e.getFullName()).orElse(u);try{return service.create(x,u,n);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
 /** Manual reconciliation of historical approved requests whose attendance was never updated. */
 @PostMapping("/{id}/apply-approved")
 public AttendanceRegularization applyApproved(@RequestHeader("Authorization") String a,@PathVariable Long id){
  String currentRole=role(a);
  if(!management(currentRole)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");
  scope.assertCanReviewById(currentRole,user(a),service.all(),id);
  try { return service.applyLegacyApproved(id); }
  catch (IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage()); }
 }
 @PatchMapping("/{id}/review") public AttendanceRegularization review(@RequestHeader("Authorization") String a,@PathVariable Long id,@RequestBody Map<String,Object>b){String currentRole=role(a);if(!management(currentRole))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");scope.assertCanReviewById(currentRole,user(a),service.all(),id);String u=user(a);String n=employees.findByRegisterEmployee_UserId(u).map(e->e.getFullName()).orElse(u);try{return service.review(id,b,u,n);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
}
