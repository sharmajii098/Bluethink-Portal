package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.CalendarOAuthState;
import EmployeeManagementSystem.repository.CalendarOAuthStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.OffsetDateTime;

@Service
public class CalendarOAuthStates {
 private final CalendarOAuthStateRepository states;private final CalendarSettings settings;
 public CalendarOAuthStates(CalendarOAuthStateRepository states,CalendarSettings settings){this.states=states;this.settings=settings;}
 @Transactional public String create(String owner,String provider,String verifier){
  states.deleteByOwnerIdAndProvider(owner,provider);states.flush();
  String raw=IntegrationCrypto.randomToken();CalendarOAuthState s=new CalendarOAuthState();s.stateHash=IntegrationCrypto.sha256(raw);s.ownerId=owner;s.provider=provider;
  s.encryptedVerifier=IntegrationCrypto.encrypt(verifier,settings.key(),owner+":"+provider+":pkce");s.expiresAt=OffsetDateTime.now().plusMinutes(10);states.save(s);return raw;
 }
 @Transactional(propagation=Propagation.REQUIRES_NEW)
 public String consume(String owner,String provider,String raw){
  if(raw==null||!raw.matches("[A-Za-z0-9_-]{43}"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid OAuth state");
  CalendarOAuthState s=states.lock(IntegrationCrypto.sha256(raw)).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"OAuth state is invalid or expired"));
  if(s.consumed||!s.ownerId.equals(owner)||!s.provider.equals(provider)||!s.expiresAt.isAfter(OffsetDateTime.now()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"OAuth state is invalid or expired");
  s.consumed=true;return IntegrationCrypto.decrypt(s.encryptedVerifier,settings.key(),owner+":"+provider+":pkce");
 }
 @Scheduled(fixedDelay=3600000) @Transactional public void cleanup(){states.deleteByExpiresAtBefore(OffsetDateTime.now());}
}
