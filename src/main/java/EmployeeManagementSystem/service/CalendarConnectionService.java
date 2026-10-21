package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class CalendarConnectionService {

 private final CalendarConnectionRepository connections;private final CalendarEventLinkRepository links;
 private final CalendarSettings settings;private final CalendarHttpClient http;private final CalendarOAuthStates states;private final PortalActor actor;
 public CalendarConnectionService(CalendarConnectionRepository connections,CalendarEventLinkRepository links,CalendarSettings settings,CalendarHttpClient http,CalendarOAuthStates states,PortalActor actor){this.connections=connections;this.links=links;this.settings=settings;this.http=http;this.states=states;this.actor=actor;}
 public record Status(String provider,boolean configured,String status,String lastSyncedAt,String message){}
 @Transactional(readOnly=true) public List<Status> status(){
  String owner=actor.id();List<Status> result=new ArrayList<>();
  for(String p:List.of("google","microsoft")){CalendarConnection c=connections.findByOwnerIdAndProvider(owner,p).orElse(null);boolean ready=settings.configured(p);result.add(new Status(p,ready,c==null?"DISCONNECTED":c.status,c==null||c.lastSyncedAt==null?null:c.lastSyncedAt.toString(),!ready?"OAuth not configured. An administrator must provide provider credentials, the exact callback URL and an encryption key before connection can open.":c==null?null:c.lastError));}return result;
 }
 public Map<String,String> begin(String provider){
  String p=settings.name(provider);CalendarSettings.Provider cfg=settings.provider(p);String verifier=IntegrationCrypto.randomToken();String state=states.create(actor.id(),p,verifier);
  Map<String,String> query=new LinkedHashMap<>();query.put("client_id",cfg.clientId());query.put("redirect_uri",cfg.redirectUri());query.put("response_type","code");query.put("scope",cfg.scope());query.put("state",state);query.put("code_challenge",IntegrationCrypto.pkce(verifier));query.put("code_challenge_method","S256");
  if(p.equals("google")){query.put("access_type","offline");query.put("prompt","consent");}else query.put("response_mode","query");
  return Map.of("authorizationUrl",cfg.authorizeUrl()+"?"+CalendarHttpClient.form(query));
 }
 @Transactional public Status complete(String provider,String rawState,String code){
  String p=settings.name(provider),owner=actor.id();CalendarSettings.Provider cfg=settings.provider(p);
  if(code==null||code.isBlank()||code.length()>8192)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid authorization code");
  String verifier=states.consume(owner,p,rawState);
  Map<String,Object> token;
  try{token=http.token(cfg,Map.of("grant_type","authorization_code","code",code,"redirect_uri",cfg.redirectUri(),"code_verifier",verifier));}
  catch(CalendarHttpClient.RemoteFailure e){throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Calendar authorization failed; reconnect and check OAuth registration");}
  CalendarConnection c=connections.lockOwned(owner,p).orElseGet(CalendarConnection::new);c.ownerId=owner;c.provider=p;
  // Reconnection may point at a different account. Never reuse old remote IDs in another account.
  if(c.id!=null){links.deleteByConnectionId(c.id);links.flush();}
  store(c,token,true);c.connectedAt=OffsetDateTime.now();c.status="CONNECTED";c.lastError=null;connections.save(c);
  return new Status(p,true,c.status,null,"Connected. Select portal meetings to synchronize");
 }
 private String context(CalendarConnection c,String part){return c.ownerId+":"+c.provider+":"+part;}
 private void store(CalendarConnection c,Map<String,Object> token,boolean newConnection){
  Object access=token.get("access_token"),refresh=token.get("refresh_token"),expires=token.get("expires_in");
  if(!(access instanceof String a)||a.isBlank()||a.length()>8000||!(expires instanceof Number))throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Invalid token response");
  if(newConnection&&(!(refresh instanceof String)||((String)refresh).isBlank()))throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Provider did not grant offline access. Reconnect with consent");
  if(newConnection){
   String granted=String.valueOf(token.getOrDefault("scope",""));
   String required=c.provider.equals("google")?"https://www.googleapis.com/auth/calendar.events.owned":"Calendars.ReadWrite";
   boolean permitted=Arrays.stream(granted.split(" ")).anyMatch(s->s.equals(required)||(!c.provider.equals("google")&&s.equals("https://graph.microsoft.com/"+required)));
   if(!permitted)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Calendar permission was not granted");
  }
  long seconds=((Number)expires).longValue();if(seconds<=0||seconds>172800)throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Invalid token expiry");
  c.encryptedAccessToken=IntegrationCrypto.encrypt(a,settings.key(),context(c,"access"));
  if(refresh instanceof String r&&r.length()>8000)throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Invalid refresh token response");
  if(refresh instanceof String r&&!r.isBlank())c.encryptedRefreshToken=IntegrationCrypto.encrypt(r,settings.key(),context(c,"refresh"));
  c.accessExpiresAt=OffsetDateTime.now().plusSeconds(seconds);
 }
 /** Caller holds the connection row lock; refresh rotation is serialized across replicas. */
 public String accessTokenLocked(CalendarConnection c){
  if(!"CONNECTED".equals(c.status))throw new CalendarHttpClient.RemoteFailure(401);
  if(c.accessExpiresAt==null||!c.accessExpiresAt.isAfter(OffsetDateTime.now().plusSeconds(90))){
   CalendarSettings.Provider cfg=settings.provider(c.provider);
   String refresh=IntegrationCrypto.decrypt(c.encryptedRefreshToken,settings.key(),context(c,"refresh"));
   if(refresh==null)throw new CalendarHttpClient.RemoteFailure(401);
   try{store(c,http.token(cfg,Map.of("grant_type","refresh_token","refresh_token",refresh)),false);}
   catch(CalendarHttpClient.RemoteFailure e){if(e.status==401){c.status="RECONNECT_REQUIRED";c.lastError="Calendar permission expired or was revoked. Reconnect";}throw e;}
  }
  return IntegrationCrypto.decrypt(c.encryptedAccessToken,settings.key(),context(c,"access"));
 }
 @Transactional public void disconnect(String provider){
  String p=settings.name(provider);CalendarConnection c=connections.lockOwned(actor.id(),p).orElse(null);
  if(c==null)return;links.deleteByConnectionId(c.id);links.flush();connections.delete(c);
  // Copies already in the external calendar are retained. Account consent is revoked at the provider.
 }
}
