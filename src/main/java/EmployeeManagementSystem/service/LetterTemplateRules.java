package EmployeeManagementSystem.service;
import java.util.*;
import java.util.regex.*;
/** Plain text only: no arbitrary expressions, HTML, scripts, filesystem or network access. */
public final class LetterTemplateRules {
 private LetterTemplateRules(){}
 public static final Set<String> TYPES=Set.of("OFFER_LETTER","EMPLOYMENT_CERTIFICATE","EXPERIENCE_LETTER","RELIEVING_LETTER","SALARY_CERTIFICATE","ADDRESS_PROOF","NO_DUES_CERTIFICATE");
 public static final Set<String> FIELDS=Set.of("NAME","EMPLOYEE_ID","DESIGNATION","JOINING_DATE","LAST_WORKING_DATE","ISSUE_DATE","ANNUAL_CTC","MONTHLY_GROSS","COMPENSATION_TABLE","PROBATION_MONTHS","NOTICE_DAYS","OFFER_EXPIRY","COMPANY_NAME","DOCUMENT_REFERENCE","ADDRESS","SIGNATORY_NAME","SIGNATORY_DESIGNATION","CLEARANCE_DATE");
 private static final Pattern PLACEHOLDER=Pattern.compile("\\{\\{([A-Z_]+)}}");
 public static void validate(String type,String body){
  if(type==null||!TYPES.contains(type)||body==null||body.isBlank()||body.length()>60000)throw new IllegalArgumentException("Select a letter type and provide 1-60000 characters of template text");
  if(body.chars().anyMatch(c->c<32&&c!='\n'&&c!='\r'&&c!='\t'&&c!='\f'))throw new IllegalArgumentException("Template contains unsupported control characters");
  Matcher m=PLACEHOLDER.matcher(body);Set<String> found=new HashSet<>();while(m.find()){if(!FIELDS.contains(m.group(1)))throw new IllegalArgumentException("Unknown template field: "+m.group(1));found.add(m.group(1));}
  if(PLACEHOLDER.matcher(body).replaceAll("").contains("{{"))throw new IllegalArgumentException("Malformed template placeholder");
  Set<String> required=new HashSet<>(Set.of("NAME","DOCUMENT_REFERENCE","ISSUE_DATE"));
  switch(type){
   case "OFFER_LETTER" -> required.addAll(Set.of("DESIGNATION","JOINING_DATE","ANNUAL_CTC","COMPENSATION_TABLE","PROBATION_MONTHS","NOTICE_DAYS","OFFER_EXPIRY"));
   case "EXPERIENCE_LETTER","RELIEVING_LETTER" -> required.addAll(Set.of("DESIGNATION","JOINING_DATE","LAST_WORKING_DATE"));
   case "EMPLOYMENT_CERTIFICATE" -> required.addAll(Set.of("DESIGNATION","JOINING_DATE"));
   case "SALARY_CERTIFICATE" -> required.addAll(Set.of("DESIGNATION","MONTHLY_GROSS"));
   case "ADDRESS_PROOF" -> required.add("ADDRESS");
   case "NO_DUES_CERTIFICATE" -> required.add("CLEARANCE_DATE");
  }
  if(!found.containsAll(required)){required.removeAll(found);throw new IllegalArgumentException("Required verified fields are missing: "+required);}
 }
 public static void approvable(String body){
  String upper=body.toUpperCase(Locale.ROOT);
  if(upper.contains("[INSERT")||upper.contains("[COMPANY-APPROVED")||upper.contains("TODO")||upper.contains("TBD")||upper.contains("PLACEHOLDER")||upper.contains("[SOURCE-REVIEW")||upper.contains("[REQUIRES VERIFICATION"))throw new IllegalArgumentException("Replace draft instructions with company-approved text before approval");
 }
 public static String render(String body,Map<String,String> values){
  Matcher matcher=PLACEHOLDER.matcher(body);StringBuffer out=new StringBuffer();
  while(matcher.find()){
   String field=matcher.group(1),value=values.get(field);
   if(value==null||value.isBlank())throw new IllegalArgumentException("Verified value missing for "+field);
   matcher.appendReplacement(out,Matcher.quoteReplacement(value));
  }
  matcher.appendTail(out);return out.toString();
 }
}
