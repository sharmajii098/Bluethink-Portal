package EmployeeManagementSystem.config;
import EmployeeManagementSystem.service.CalendarSettings;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
/** Reject known-insecure production configuration rather than silently degrading. */
@Component @Profile("prod")
public class ProductionReleaseGuard implements InitializingBean {
 private final Environment env;private final CalendarSettings calendar;
 public ProductionReleaseGuard(Environment env,CalendarSettings calendar){this.env=env;this.calendar=calendar;}
 private String value(String key){return env.getProperty(key,"");}
 private void require(boolean valid,String message){if(!valid)throw new IllegalStateException(message);}
 @Override public void afterPropertiesSet(){
  String key=value("security.jwt.secret");
  require(key.length()>=48&&!key.contains("local-development")&&!key.contains("change-before"),"Production JWT secret must be independently generated (48+ characters)");
  require("validate".equals(value("spring.jpa.hibernate.ddl-auto")),"Run reviewed migrations first; production schema mode must be validate");
  require(!Boolean.parseBoolean(value("hrms.bootstrap-admin.enabled")),"Disable bootstrap administrator in production");
  require(Boolean.parseBoolean(value("bluethink.chat.redis-enabled")),"Production requires Redis-backed throttling");
  require(Boolean.parseBoolean(value("chat.antivirus.required")),"Production requires fail-closed attachment scanning");
  require(value("portal.frontend-url").startsWith("https://"),"Production frontend URL must use HTTPS");
  for(String provider:java.util.List.of("google","microsoft"))if(Boolean.parseBoolean(value("bluethink.calendar."+provider+".enabled"))){
   CalendarSettings.Provider p=calendar.provider(provider);require(p.redirectUri().startsWith("https://"),"Production calendar callbacks require HTTPS");
  }
 }
}
