package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.EmployeeAsset;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.service.EmployeeAssetService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController @RequestMapping("/api/assets")
public class EmployeeAssetController {
    private final EmployeeAssetService service; private final JwtUtil jwt; private final EmployeeRepository employees;
    public EmployeeAssetController(EmployeeAssetService service, JwtUtil jwt, EmployeeRepository employees){this.service=service;this.jwt=jwt;this.employees=employees;}
    private String token(String a){if(a==null||!a.startsWith("Bearer "))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required.");return a.substring(7);}
    private String user(String a){return jwt.extractUsername(token(a));}
    private String role(String a){String r=jwt.extractRole(token(a));return r==null?"":r.replace("ROLE_","").toUpperCase();}
    private void manage(String r){if(!Set.of("ADMIN","HR").contains(r))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Asset management access required.");}
    @GetMapping("/my") public List<EmployeeAsset> my(@RequestHeader("Authorization") String a){return service.my(user(a));}
    @GetMapping("/all") public List<EmployeeAsset> all(@RequestHeader("Authorization") String a){manage(role(a));return service.all();}
    @PostMapping public EmployeeAsset create(@RequestHeader("Authorization") String a,@RequestBody EmployeeAsset x){manage(role(a));try{return service.create(x);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
    @PutMapping("/{id}") public EmployeeAsset update(@PathVariable Long id,@RequestHeader("Authorization") String a,@RequestBody EmployeeAsset x){manage(role(a));try{return service.update(id,x);}catch(NoSuchElementException e){throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
    @PatchMapping("/{id}/assign") public EmployeeAsset assign(@PathVariable Long id,@RequestHeader("Authorization") String a,@RequestBody Map<String,String> b){manage(role(a));String u=b.get("userId");if(u==null||u.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"userId is required.");String n=employees.findByRegisterEmployee_UserId(u).map(x->x.getFullName()).orElse(u);try{return service.assign(id,u,n);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
    @PatchMapping("/{id}/return") public EmployeeAsset returnAsset(@PathVariable Long id,@RequestHeader("Authorization") String a){manage(role(a));try{return service.returnAsset(id);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
    @PatchMapping("/{id}/maintenance") public EmployeeAsset maintenance(@PathVariable Long id,@RequestHeader("Authorization") String a){manage(role(a));try{return service.markMaintenance(id);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
    @PatchMapping("/{id}/available") public EmployeeAsset available(@PathVariable Long id,@RequestHeader("Authorization") String a){manage(role(a));try{return service.markAvailable(id);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
    @DeleteMapping("/{id}") public Map<String,Object> delete(@PathVariable Long id,@RequestHeader("Authorization") String a){manage(role(a));try{service.delete(id);return Map.of("success",true);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}}
}
