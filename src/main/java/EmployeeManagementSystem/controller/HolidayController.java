package EmployeeManagementSystem.controller;
import EmployeeManagementSystem.entity.Holiday;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.service.HolidayService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate; import java.util.*;
@RestController @RequestMapping("/api/holidays")
public class HolidayController {
 private final HolidayService service; private final JwtUtil jwt;
 public HolidayController(HolidayService s,JwtUtil j){service=s;jwt=j;}
 private String token(String a){if(a==null||!a.startsWith("Bearer "))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required.");return a.substring(7);}
 private String role(String a){String r=jwt.extractRole(token(a));return r==null?"":r.replace("ROLE_","").toUpperCase();}
 private boolean mgmt(String r){return Set.of("ADMIN","HR").contains(r);}
 private void requireMgmt(String a){if(!mgmt(role(a)))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"HR/Admin access required.");}
 private RuntimeException bad(Exception e){if(e instanceof ResponseStatusException)return (ResponseStatusException)e;return new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}
 @GetMapping public List<Holiday> all(@RequestHeader("Authorization") String a){try{return service.all().stream().filter(h->!Boolean.FALSE.equals(h.getActive())).toList();}catch(Exception e){throw bad(e);}}
 @GetMapping("/manage") public List<Holiday> manage(@RequestHeader("Authorization") String a){requireMgmt(a);return service.all();}
 @PostMapping public Holiday create(@RequestHeader("Authorization") String a,@RequestBody Holiday x){requireMgmt(a);try{return service.create(x);}catch(Exception e){throw bad(e);}}
 @PutMapping("/{id}") public Holiday update(@PathVariable Long id,@RequestHeader("Authorization") String a,@RequestBody Holiday x){requireMgmt(a);try{return service.update(id,x);}catch(Exception e){throw bad(e);}}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id,@RequestHeader("Authorization") String a){requireMgmt(a);try{service.delete(id);return ResponseEntity.noContent().build();}catch(Exception e){throw bad(e);}}
}
