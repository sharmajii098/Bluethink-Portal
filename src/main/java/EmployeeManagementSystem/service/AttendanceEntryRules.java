package EmployeeManagementSystem.service;
import java.time.*;
import java.util.Set;
public final class AttendanceEntryRules {
 private AttendanceEntryRules(){}
 public static double validate(LocalDate date,LocalDateTime in,LocalDateTime out,String status,LocalDateTime now){
  if(date==null||date.isAfter(now.toLocalDate()))throw new IllegalArgumentException("Future attendance cannot be recorded.");
  if(in!=null&&(!in.toLocalDate().equals(date)||in.isAfter(now)))throw new IllegalArgumentException("Check-in must match the attendance date and cannot be in the future.");
  if(out!=null&&(in==null||!out.isAfter(in)||out.isAfter(now)))throw new IllegalArgumentException("Check-out must follow check-in and cannot be in the future.");
  boolean worked=Set.of("PRESENT","LATE","HALF_DAY").contains(status);
  if(worked&&(in==null||out==null))throw new IllegalArgumentException("Worked attendance requires both punch times.");
  if(!worked&&(in!=null||out!=null))throw new IllegalArgumentException("Non-working attendance cannot include punch times.");
  return in!=null&&out!=null?Math.round(Duration.between(in,out).toMinutes()/60.0*100.0)/100.0:0;
 }
}
