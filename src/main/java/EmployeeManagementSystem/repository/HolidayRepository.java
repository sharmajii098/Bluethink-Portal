package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

 List<Holiday> findByActiveTrueAndHolidayDateBetweenOrderByHolidayDateAsc(
         LocalDate from,
         LocalDate to
 );

 List<Holiday> findAllByOrderByHolidayDateAsc();

 // FIX: Required by HolidayService
 boolean existsByHolidayDate(LocalDate holidayDate);

 boolean existsByHolidayDateAndNameIgnoreCaseAndLocationIgnoreCase(
         LocalDate date,
         String name,
         String location
 );

 List<Holiday> findByHolidayDateBetween(
         LocalDate from,
         LocalDate to
 );
}