package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.HrLetterTemplate;
import EmployeeManagementSystem.repository.HrLetterTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.*;
@Service
public class HrLetterTemplateService {
 @org.springframework.beans.factory.annotation.Autowired private ReleaseAudit audit;

 private final HrLetterTemplateRepository templates;private final PortalActor actor;
 public HrLetterTemplateService(HrLetterTemplateRepository templates,PortalActor actor){this.templates=templates;this.actor=actor;}
 public Map<String,String> starters(){actor.hr();return HrLetterStarters.all();}
 public record Input(String templateCode,int revision,String letterType,String title,String companyName,String companyAddress,String verificationEmail,String cin,String signatoryName,String signatoryDesignation,String body){}
 @Transactional(readOnly=true)public List<HrLetterTemplate> list(){actor.hr();return templates.findAllByOrderByCreatedAtDesc();}
 private String text(String s,int max,String name){if(s==null||s.isBlank()||s.length()>max||s.chars().anyMatch(c->c<32||c==127))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid "+name);return s.trim();}
 @Transactional public HrLetterTemplate create(Input i){
  actor.hr();if(i==null||i.templateCode()==null||!i.templateCode().matches("[A-Z0-9_-]{3,60}")||i.revision()<1)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Use a unique template code and positive revision");
  LetterTemplateRules.validate(i.letterType(),i.body());HrLetterTemplate t=new HrLetterTemplate();t.templateCode=i.templateCode();t.revision=i.revision();t.letterType=i.letterType();
  t.title=text(i.title(),140,"title");t.companyName=text(i.companyName(),200,"company name");t.companyAddress=text(i.companyAddress(),1000,"company address");t.verificationEmail=MeetingRecipients.normalize(i.verificationEmail());
  t.cin=i.cin()==null||i.cin().isBlank()?"":text(i.cin(),80,"CIN");t.signatoryName=text(i.signatoryName(),120,"signatory name");t.signatoryDesignation=text(i.signatoryDesignation(),120,"signatory designation");t.body=i.body().replace("\r\n","\n");
  t.createdBy=actor.id();t.createdAt=OffsetDateTime.now();HrLetterTemplate saved=templates.save(t);audit.record("HR_TEMPLATE","CREATE",saved.id);return saved;
 }

 @Transactional public HrLetterTemplate update(Long id,Input i){
  actor.hr();if(i==null)throw new IllegalArgumentException("Template input is required");HrLetterTemplate old=templates.lock(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  if(!"DRAFT".equals(old.status)||!actor.id().equals(old.createdBy))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the author may edit an unapproved draft. Copy an approved template to a new revision");
  if(!old.templateCode.equals(i.templateCode())||old.revision!=i.revision())throw new IllegalArgumentException("Template code and revision cannot change in place");
  LetterTemplateRules.validate(i.letterType(),i.body());old.letterType=i.letterType();old.title=text(i.title(),140,"title");old.companyName=text(i.companyName(),200,"company name");old.companyAddress=text(i.companyAddress(),1000,"company address");old.verificationEmail=MeetingRecipients.normalize(i.verificationEmail());old.cin=i.cin()==null||i.cin().isBlank()?"":text(i.cin(),80,"CIN");old.signatoryName=text(i.signatoryName(),120,"signatory name");old.signatoryDesignation=text(i.signatoryDesignation(),120,"signatory designation");old.body=i.body().replace("\r\n","\n");audit.record("HR_TEMPLATE","UPDATE",old.id);return templates.save(old);
 }
 @Transactional public HrLetterTemplate approve(Long id){
  actor.hr();HrLetterTemplate t=templates.lock(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  if(!"DRAFT".equals(t.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"Approved template versions are immutable; create a new revision");
  if(t.createdBy.equals(actor.id()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"A different HR/Admin must approve the template");
  LetterTemplateRules.validate(t.letterType,t.body);LetterTemplateRules.approvable(t.body);t.status="APPROVED";t.approvedBy=actor.id();t.approvedAt=OffsetDateTime.now();audit.record("HR_TEMPLATE","APPROVE",t.id);return templates.save(t);
 }
}
