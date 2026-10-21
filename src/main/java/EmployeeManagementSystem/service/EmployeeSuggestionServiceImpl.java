package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeSuggestion;
import EmployeeManagementSystem.repository.EmployeeSuggestionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class EmployeeSuggestionServiceImpl implements EmployeeSuggestionService{
    private final EmployeeSuggestionRepository repo;
    public EmployeeSuggestionServiceImpl(EmployeeSuggestionRepository r){repo=r;}
    public List<EmployeeSuggestion> my(String u){return repo.findByUserIdOrderByCreatedAtDesc(u);}
    public List<EmployeeSuggestion> all(){return repo.findAllByOrderByCreatedAtDesc();}
    private void validate(EmployeeSuggestion x){
        if(x.getCategory()==null||x.getCategory().isBlank()) throw new IllegalArgumentException("Category is required.");
        if(x.getSubject()==null||x.getSubject().isBlank()) throw new IllegalArgumentException("Subject is required.");
        if(x.getDescription()==null||x.getDescription().isBlank()) throw new IllegalArgumentException("Description is required.");
        if(x.getSubject().length()>200) throw new IllegalArgumentException("Subject must be 200 characters or less.");
        if(x.getDescription().length()>4000) throw new IllegalArgumentException("Description must be 4000 characters or less.");
        if(x.getPriority()==null||!Set.of("LOW","MEDIUM","HIGH").contains(x.getPriority().toUpperCase())) throw new IllegalArgumentException("Invalid priority.");
    }
    public EmployeeSuggestion create(EmployeeSuggestion x,String u,String n){
        validate(x); x.setId(null); x.setSuggestionNumber("SUG-"+System.currentTimeMillis()); x.setUserId(u); x.setEmployeeName(x.isAnonymous()?"Anonymous":n); x.setPriority(x.getPriority().toUpperCase()); x.setStatus("SUBMITTED"); x.setManagementResponse(null); x.setReviewedBy(null); x.setReviewedAt(null); return repo.save(x);
    }
    public EmployeeSuggestion review(Long id,String reviewer,String status,String response){
        EmployeeSuggestion e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Suggestion not found."));
        if(!Set.of("UNDER_REVIEW","IMPLEMENTED","CLOSED","REJECTED").contains(status)) throw new IllegalArgumentException("Invalid review status.");
        e.setStatus(status); e.setReviewedBy(reviewer); e.setManagementResponse(response); e.setReviewedAt(LocalDateTime.now()); return repo.save(e);
    }
}
