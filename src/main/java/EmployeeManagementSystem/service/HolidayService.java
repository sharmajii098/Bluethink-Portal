package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.Holiday;
import java.time.LocalDate;
import java.util.*;
public interface HolidayService {
 List<Holiday> upcoming(LocalDate from, LocalDate to);
 List<Holiday> all();
 Holiday create(Holiday x);
 Holiday update(Long id, Holiday x);
 void delete(Long id);
}
