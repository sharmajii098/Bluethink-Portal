package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.LeaveRequest;
import EmployeeManagementSystem.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    // =========================================================
    // Employee leave history
    // =========================================================

    List<LeaveRequest> findByEmployeeId(String employeeId);


    // =========================================================
    // Count leaves by status
    // =========================================================

    long countByStatus(LeaveStatus status);


    // =========================================================
    // Employee leaves by status
    // =========================================================

    List<LeaveRequest> findByEmployeeIdAndStatus(
            String employeeId,
            LeaveStatus status
    );


    // =========================================================
    // Employee calendar - overlapping date range
    // =========================================================

    List<LeaveRequest> findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String employeeId,
            LocalDate calendarEndDate,
            LocalDate calendarStartDate
    );


    // =========================================================
    // Check whether employee already has overlapping leave
    // =========================================================

    boolean existsByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String employeeId,
            LocalDate endDate,
            LocalDate startDate
    );


    // =========================================================
    // Admin calendar - all overlapping leaves
    // =========================================================

    List<LeaveRequest> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate calendarEndDate,
            LocalDate calendarStartDate
    );


    // =========================================================
    // Leave type
    // =========================================================

    List<LeaveRequest> findByLeaveType(
            String leaveType
    );


    // =========================================================
    // Created date range
    // =========================================================

    List<LeaveRequest> findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}