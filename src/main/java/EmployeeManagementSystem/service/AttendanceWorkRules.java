package EmployeeManagementSystem.service;
import java.time.Duration;
import java.time.LocalDateTime;
/** Pure attendance calculations, with explicit policy inputs and no implicit unpaid break. */
public final class AttendanceWorkRules {
 private AttendanceWorkRules(){}
 public record Result(long workedMinutes,long lateMinutes,long overtimeMinutes,boolean late){}
 /** Common duration rule used when saving and displaying punch records; overnight punches are supported. */
 public static long workedMinutes(LocalDateTime checkIn, LocalDateTime checkOut, int unpaidBreakMinutes) {
  if(checkIn==null||checkOut==null||unpaidBreakMinutes<0||unpaidBreakMinutes>720)throw new IllegalArgumentException("Invalid punch or break");
  long gross=Duration.between(checkIn,checkOut).toMinutes();
  if(gross<0||gross>1440)throw new IllegalArgumentException("Punch duration must be 0-24 hours");
  return Math.max(0,gross-unpaidBreakMinutes);
 }
 public static Result calculate(LocalDateTime checkIn, LocalDateTime checkOut, LocalDateTime shiftStart, int graceMinutes, int standardMinutes, int breakMinutes) {
  if(checkIn==null||checkOut==null||shiftStart==null||graceMinutes<0||graceMinutes>180||standardMinutes<1||standardMinutes>1440||breakMinutes<0||breakMinutes>720||checkOut.isBefore(checkIn))throw new IllegalArgumentException("Invalid attendance times or policy");
  long worked=workedMinutes(checkIn,checkOut,breakMinutes);long late=Math.max(0,Duration.between(shiftStart,checkIn).toMinutes()-graceMinutes);
  return new Result(worked,late,Math.max(0,worked-standardMinutes),late>0);
 }
}
