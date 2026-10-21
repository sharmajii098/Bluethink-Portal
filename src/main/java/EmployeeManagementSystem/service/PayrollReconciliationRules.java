package EmployeeManagementSystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/** Pure payroll rules. No statutory rates or company policies are invented by this class. */
public final class PayrollReconciliationRules {
 private PayrollReconciliationRules(){}
 public record Policy(String reference,Set<DayOfWeek> workingDays,int fullDayMinutes,int halfDayMinutes,
       boolean requireApprovedTimesheet,int cutoffDay,Map<String,String> leaveTreatment){
  public Policy{
   if(reference==null||reference.isBlank()||reference.length()>160||workingDays==null||workingDays.isEmpty()||
      fullDayMinutes<60||fullDayMinutes>1440||halfDayMinutes<=0||halfDayMinutes>fullDayMinutes||cutoffDay<1||cutoffDay>28||leaveTreatment==null)
       throw new IllegalArgumentException("Complete an approved policy reference, working days, minute thresholds, leave treatment and cutoff day");
   workingDays=Collections.unmodifiableSet(EnumSet.copyOf(workingDays));Map<String,String> normalized=new TreeMap<>();
   leaveTreatment.forEach((k,v)->{if(k==null||k.isBlank()||v==null||!Set.of("PAID","UNPAID","PROJECT_OFF").contains(v))throw new IllegalArgumentException("Each leave type needs PAID, UNPAID or PROJECT_OFF treatment");normalized.put(k.trim().toUpperCase(Locale.ROOT),v);});
   leaveTreatment=Collections.unmodifiableMap(normalized);
  }
 }
 public record Decision(double payableUnits,String reason){
  public Decision{if(!Set.of(0.0,0.5,1.0).contains(payableUnits)||reason==null||reason.trim().length()<10||reason.length()>400)throw new IllegalArgumentException("Day overrides need 0, 0.5 or 1 payable day and a 10-400 character reason");}
 }
 public record Input(LocalDate date,boolean scheduled,boolean employed,int fullMinutes,String status,Integer workedMinutes,
       boolean openSession,int attendanceRecords,boolean approvedTimesheet,double paidLeave,double unpaidLeave,double projectOff,
       List<String> sourceIssues,Decision override){}
 public record Day(LocalDate date,boolean scheduled,boolean employed,double present,double paidLeave,double unpaidLeave,
       double projectOff,double absent,double payable,String basis,List<String> issues,List<String> resolvedIssues){}
 public static Day day(Input i,Policy p){
  if(i.date()==null)throw new IllegalArgumentException("Missing payroll date");
  if(!i.scheduled()||!i.employed())return new Day(i.date(),i.scheduled(),i.employed(),0,0,0,0,0,0,i.employed()?"NON_WORKING_DAY":"OUTSIDE_EMPLOYMENT",List.of(),List.of());
  List<String> issues=new ArrayList<>(i.sourceIssues()==null?List.of():i.sourceIssues());
  double leave=i.paidLeave()+i.unpaidLeave()+i.projectOff();
  if(!Double.isFinite(leave)||i.paidLeave()<0||i.unpaidLeave()<0||i.projectOff()<0||leave>1.0||leave<0)issues.add("OVERLAPPING_LEAVE");
  if(i.attendanceRecords()>1)issues.add("DUPLICATE_ATTENDANCE");
  if(i.openSession())issues.add("OPEN_PUNCH");
  if(i.fullMinutes()<=0||i.fullMinutes()>1440)issues.add("INVALID_SHIFT");
  String status=i.status()==null?"MISSING":i.status();double present=0;
  if(leave<1.0){
   if(Set.of("PRESENT","LATE","HALF_DAY").contains(status)){
    if(i.workedMinutes()==null||i.workedMinutes()<0||i.workedMinutes()>1440)issues.add("INVALID_WORK_DURATION");
    else{
     int full=i.fullMinutes()>0?i.fullMinutes():p.fullDayMinutes();int half=(int)Math.ceil((double)full*p.halfDayMinutes()/p.fullDayMinutes());
     present=i.workedMinutes()>=full?1:i.workedMinutes()>=half?0.5:0;
     if("HALF_DAY".equals(status))present=Math.min(present,0.5);
     if(present==0)issues.add("SHORT_ATTENDANCE_REQUIRES_REVIEW");
     if(p.requireApprovedTimesheet()&&!i.approvedTimesheet())issues.add("APPROVED_TIMESHEET_MISSING");
    }
   }else if(!"ABSENT".equals(status))issues.add("UNVERIFIED".equals(status)?"UNVERIFIED_ATTENDANCE":"ATTENDANCE_MISSING_OR_INCONSISTENT");
  }
  present=Math.min(present,Math.max(0,1-leave));
  double paid=Math.max(0,Math.min(1,present+i.paidLeave()+i.projectOff()));
  double absent=Math.max(0,1-present-leave);
  if(i.override()!=null){
   Decision d=i.override();return new Day(i.date(),true,true,d.payableUnits(),0,0,0,1-d.payableUnits(),d.payableUnits(),"HR_OVERRIDE: "+d.reason(),List.of(),List.copyOf(issues));
  }
  return new Day(i.date(),true,true,present,i.paidLeave(),i.unpaidLeave(),i.projectOff(),absent,paid,"ATTENDANCE_AND_APPROVED_LEAVE",List.copyOf(issues),List.of());
 }
 public record Totals(double scheduled,double eligible,double present,double paidLeave,double unpaidLeave,double projectOff,double absent,
       double payable,double nonEmploymentDays,BigDecimal monthlyGross,BigDecimal overtimePay,BigDecimal lossOfPay,
       BigDecimal statutoryAndOtherDeductions,BigDecimal totalDeductions,BigDecimal netPay){}
 public static Totals totals(List<Day> days,BigDecimal gross,BigDecimal deductions,BigDecimal approvedOvertimeHours,BigDecimal overtimeRate){
  for(BigDecimal v:Arrays.asList(gross,deductions,approvedOvertimeHours,overtimeRate))if(v==null||v.signum()<0)throw new IllegalArgumentException("Payroll amounts cannot be negative");
  double scheduled=0,eligible=0,present=0,paidLeave=0,unpaid=0,project=0,absent=0,payable=0;
  for(Day d:days){if(d.scheduled())scheduled++;if(d.scheduled()&&d.employed())eligible++;present+=d.present();paidLeave+=d.paidLeave();unpaid+=d.unpaidLeave();project+=d.projectOff();absent+=d.absent();payable+=d.payable();}
  if(scheduled==0)throw new IllegalArgumentException("No scheduled working days; review payroll policy and roster");
  if(payable<0||payable>scheduled)throw new IllegalArgumentException("Invalid payable days");
  BigDecimal earned=gross.multiply(BigDecimal.valueOf(payable)).divide(BigDecimal.valueOf(scheduled),2,RoundingMode.HALF_UP);
  BigDecimal loss=gross.subtract(earned).setScale(2,RoundingMode.HALF_UP);
  BigDecimal overtime=approvedOvertimeHours.multiply(overtimeRate).setScale(2,RoundingMode.HALF_UP);
  BigDecimal total=deductions.add(loss).setScale(2,RoundingMode.HALF_UP);
  BigDecimal net=gross.add(overtime).subtract(total).setScale(2,RoundingMode.HALF_UP);
  if(net.signum()<0)throw new IllegalArgumentException("Deductions exceed earned salary; HR must correct the approved amounts");
  return new Totals(scheduled,eligible,present,paidLeave,unpaid,project,absent,payable,scheduled-eligible,gross.setScale(2,RoundingMode.HALF_UP),overtime,loss,deductions,total,net);
 }
}
