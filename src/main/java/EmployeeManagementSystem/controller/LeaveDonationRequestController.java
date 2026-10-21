package EmployeeManagementSystem.controller;
import EmployeeManagementSystem.entity.LeaveDonationRequest;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.service.LeaveDonationRequestService; import EmployeeManagementSystem.service.RequestScopeService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController @RequestMapping("/api/leave-donations")
public class LeaveDonationRequestController{
    private final LeaveDonationRequestService service; private final JwtUtil jwt; private final EmployeeRepository employees; private final RequestScopeService scope;
    public LeaveDonationRequestController(LeaveDonationRequestService s,JwtUtil j,EmployeeRepository e,RequestScopeService requestScope){service=s;jwt=j;employees=e;scope=requestScope;}
    private String tok(String a){if(a==null||!a.startsWith("Bearer "))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required.");return a.substring(7);}
    private String user(String a){return jwt.extractUsername(tok(a));}
    private String role(String a){String r=jwt.extractRole(tok(a));return r==null?"":r.replace("ROLE_","").toUpperCase();}
    private boolean mgmt(String r){return Set.of("ADMIN","HR","MANAGER").contains(r);}
    @GetMapping("/my") public List<LeaveDonationRequest> my(@RequestHeader("Authorization")String a){return service.my(user(a));}
    @GetMapping public List<LeaveDonationRequest> all(@RequestHeader("Authorization")String a){String currentRole=role(a);if(!mgmt(currentRole))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");return scope.visibleTo(currentRole,user(a),service.all());}
    private Optional<EmployeeManagementSystem.entity.Employee> employeeByUserId(String userId){
        Optional<EmployeeManagementSystem.entity.Employee> employee=employees.findByRegisterEmployee_UserId(userId);
        return employee.isPresent()?employee:employees.findByEmployeeProfile_UserId(userId);
    }
    @PostMapping public LeaveDonationRequest create(@RequestHeader("Authorization")String a,@RequestBody LeaveDonationRequest x){String u=user(a);String n=employeeByUserId(u).map(e->e.getFullName()).filter(v->v!=null&&!v.isBlank()).orElse(u);String rn=x.getRecipientUserId();String recipientName=employeeByUserId(rn).map(e->e.getFullName()).filter(v->v!=null&&!v.isBlank()).orElse(rn);try{return service.create(x,u,n,recipientName);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
    @PatchMapping("/{id}/review") public LeaveDonationRequest review(@RequestHeader("Authorization")String a,@PathVariable Long id,@RequestBody Map<String,Object>b){String currentRole=role(a);if(!mgmt(currentRole))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");scope.assertCanReviewById(currentRole,user(a),service.all(),id);try{return service.review(id,String.valueOf(b.get("status")),String.valueOf(b.getOrDefault("comment","")),user(a));}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
}
