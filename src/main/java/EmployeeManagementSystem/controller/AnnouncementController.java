package EmployeeManagementSystem.controller;
import EmployeeManagementSystem.entity.Announcement;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.service.AnnouncementService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController @RequestMapping("/api/announcements")
public class AnnouncementController {
 private final AnnouncementService service; private final JwtUtil jwt;
 public AnnouncementController(AnnouncementService s,JwtUtil j){service=s;jwt=j;}
 private String token(String a){if(a==null||!a.startsWith("Bearer "))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required.");return a.substring(7);}
 private String user(String a){return jwt.extractUsername(token(a));} private String role(String a){String r=jwt.extractRole(token(a));return r==null?"":r.replace("ROLE_","").toUpperCase();}
 private void mgmt(String r){if(!"ADMIN".equals(r)&&!"HR".equals(r))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Management access required.");}
 @GetMapping public Object list(@RequestHeader("Authorization") String a){return service.listForUser(user(a),role(a));}
 @GetMapping("/all") public List<Announcement> all(@RequestHeader("Authorization") String a){mgmt(role(a));return service.listAll();}
 @PostMapping public Announcement create(@RequestHeader("Authorization") String a,@RequestBody Announcement x){mgmt(role(a));try{return service.create(x,user(a));}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
 @PutMapping("/{id}") public Announcement update(@PathVariable Long id,@RequestHeader("Authorization") String a,@RequestBody Announcement x){mgmt(role(a));try{return service.update(id,x);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
 @DeleteMapping("/{id}") public Map<String,Object> delete(@PathVariable Long id,@RequestHeader("Authorization") String a){mgmt(role(a));service.delete(id);return Map.of("success",true);}
 @PostMapping("/{id}/read") public Map<String,Object> read(@PathVariable Long id,@RequestHeader("Authorization") String a,@RequestParam(defaultValue="false") boolean acknowledge){return service.markRead(id,user(a),acknowledge);}
}
