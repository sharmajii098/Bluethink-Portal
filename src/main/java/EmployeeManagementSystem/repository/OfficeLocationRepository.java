package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.OfficeLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficeLocationRepository
        extends JpaRepository<OfficeLocation, Long> {

    Optional<OfficeLocation> findByOfficeName(String officeName);
}