package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.LeaveEncashmentRequest;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.service.LeaveEncashmentService; import EmployeeManagementSystem.service.RequestScopeService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api/leave-encashments")
public class LeaveEncashmentController {
    private final LeaveEncashmentService service; private final JwtUtil jwt; private final EmployeeRepository employees; private final RequestScopeService scope;
    public LeaveEncashmentController(LeaveEncashmentService s, JwtUtil j, EmployeeRepository e,RequestScopeService requestScope){service=s;jwt=j;employees=e;scope=requestScope;}
    private String token(String a){if(a==null||!a.startsWith("Bearer "))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required.");return a.substring(7);}
    private String user(String a){return jwt.extractUsername(token(a));}
    private String role(String a){String r=jwt.extractRole(token(a));return r==null?"":r.replace("ROLE_","").toUpperCase();}
    private boolean mgmt(String r){return Set.of("ADMIN","HR","MANAGER").contains(r);}
    @GetMapping("/my") public List<LeaveEncashmentRequest> my(@RequestHeader("Authorization") String a){return service.my(user(a));}
    @GetMapping public List<LeaveEncashmentRequest> all(@RequestHeader("Authorization") String a){String currentRole=role(a);if(!mgmt(currentRole))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");return scope.visibleTo(currentRole,user(a),service.all());}
    @PostMapping public LeaveEncashmentRequest create(@RequestHeader("Authorization") String a,@RequestBody LeaveEncashmentRequest x){String u=user(a);String n=employees.findByRegisterEmployee_UserId(u).map(e->e.getFullName()).orElse(u);try{return service.create(x,u,n);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
    @PatchMapping("/{id}/review") public LeaveEncashmentRequest review(@PathVariable Long id,@RequestHeader("Authorization") String a,@RequestBody Map<String,Object> b){String currentRole=role(a);if(!mgmt(currentRole))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");scope.assertCanReviewById(currentRole,user(a),service.all(),id);try{return service.review(id,String.valueOf(b.get("status")),String.valueOf(b.getOrDefault("comment","")),user(a));}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
}
