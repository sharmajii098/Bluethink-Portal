package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.ShiftSwapRequest;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.service.RequestScopeService;
import EmployeeManagementSystem.service.ShiftSwapRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/shift-swaps")
public class ShiftSwapRequestController {
    private final ShiftSwapRequestService service;
    private final JwtUtil jwt;
    private final EmployeeRepository employees;
    private final RequestScopeService scope;
    public ShiftSwapRequestController(ShiftSwapRequestService service, JwtUtil jwt,
                                      EmployeeRepository employees, RequestScopeService scope) {
        this.service=service;this.jwt=jwt;this.employees=employees;this.scope=scope;
    }
    private String token(String header) {
        if(header==null || !header.startsWith("Bearer "))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required.");
        return header.substring(7);
    }
    private String user(String header){return jwt.extractUsername(token(header));}
    private String role(String header){String r=jwt.extractRole(token(header));return r==null?"":r.replace("ROLE_","").toUpperCase(Locale.ROOT);}
    private void management(String role){if(!Set.of("ADMIN","HR","MANAGER").contains(role))
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");}
    private String requestRole(ShiftSwapRequest req){
        return employees.findByRegisterEmployee_UserId(req.getRequesterUserId())
                .map(e->e.getRegisterEmployee()==null?"EMPLOYEE":e.getRegisterEmployee().getRole())
                .orElse("EMPLOYEE").replace("ROLE_","").toUpperCase(Locale.ROOT);
    }
    private void approveScope(String role,String reviewer,ShiftSwapRequest req) {
        // Admin only approves requests made by managers/HR, never employee swap requests.
        if("ADMIN".equals(role) && !Set.of("MANAGER","HR").contains(requestRole(req)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Employee shift swaps are handled by HR and managers.");
        scope.assertCanReview(role,reviewer,req);
    }
    @GetMapping("/my") public List<ShiftSwapRequest> my(@RequestHeader("Authorization")String a){return service.my(user(a));}
    @GetMapping public List<ShiftSwapRequest> all(@RequestHeader("Authorization")String a){
        String r=role(a);management(r);List<ShiftSwapRequest> rows=scope.visibleTo(r,user(a),service.all());
        if("ADMIN".equals(r)) return rows.stream().filter(x->Set.of("MANAGER","HR").contains(requestRole(x))).toList();
        return rows;
    }
    @GetMapping("/eligible") public List<Map<String,Object>> eligible(@RequestHeader("Authorization")String a,
            @RequestParam LocalDate requesterDate,@RequestParam LocalDate targetDate){
        try{return service.eligible(user(a),requesterDate,targetDate);}
        catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());}
    }
    @GetMapping("/preview") public Map<String,Object> preview(@RequestHeader("Authorization")String a,
            @RequestParam String targetUserId,@RequestParam LocalDate requesterDate,@RequestParam LocalDate targetDate){
        try{return service.preview(user(a),targetUserId,requesterDate,targetDate);}
        catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());}
    }
    @PostMapping public ShiftSwapRequest create(@RequestHeader("Authorization")String a,@RequestBody ShiftSwapRequest x){
        try{return service.create(x,user(a),"");}
        catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());}
    }
    @PatchMapping("/{id}/consent") public ShiftSwapRequest consent(@RequestHeader("Authorization")String a,
            @PathVariable Long id,@RequestBody Map<String,Object> b){
        try{
            if(!(b.get("accept") instanceof Boolean accepted)) throw new IllegalArgumentException("accept must be true or false.");
            return service.consent(id,user(a),accepted,b.get("comment")==null?"":String.valueOf(b.get("comment")));
        }catch(SecurityException ex){throw new ResponseStatusException(HttpStatus.FORBIDDEN,ex.getMessage());}
        catch(NoSuchElementException ex){throw new ResponseStatusException(HttpStatus.NOT_FOUND,ex.getMessage());}
        catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());}
    }
    @PatchMapping("/{id}/review") public ShiftSwapRequest review(@RequestHeader("Authorization")String a,
            @PathVariable Long id,@RequestBody Map<String,Object> b){
        String r=role(a);management(r);
        ShiftSwapRequest req=service.all().stream().filter(x->x.getId().equals(id)).findFirst()
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Request not found."));
        approveScope(r,user(a),req);
        try{return service.review(id,b,user(a));}
        catch(NoSuchElementException ex){throw new ResponseStatusException(HttpStatus.NOT_FOUND,ex.getMessage());}
        catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.CONFLICT,ex.getMessage());}
    }
}
