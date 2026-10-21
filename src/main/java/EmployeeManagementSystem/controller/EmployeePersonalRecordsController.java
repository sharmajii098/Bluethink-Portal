package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.EmployeeEducationRecord;
import EmployeeManagementSystem.entity.EmployeeEmploymentHistory;
import EmployeeManagementSystem.entity.EmployeeFamilyMember;
import EmployeeManagementSystem.entity.EmployeeProfile;
import EmployeeManagementSystem.repository.EmployeeEducationRecordRepository;
import EmployeeManagementSystem.repository.EmployeeEmploymentHistoryRepository;
import EmployeeManagementSystem.repository.EmployeeFamilyMemberRepository;
import EmployeeManagementSystem.repository.EmployeeProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/profile/me")
public class EmployeePersonalRecordsController {

    private final EmployeeProfileRepository profiles;
    private final EmployeeEducationRecordRepository educationRepo;
    private final EmployeeEmploymentHistoryRepository employmentRepo;
    private final EmployeeFamilyMemberRepository familyRepo;
    private final Path photoRoot;

    public EmployeePersonalRecordsController(
            EmployeeProfileRepository profiles,
            EmployeeEducationRecordRepository educationRepo,
            EmployeeEmploymentHistoryRepository employmentRepo,
            EmployeeFamilyMemberRepository familyRepo,
            @Value("${app.profile-photo.dir:uploads/profile-photos}") String photoDir) {
        this.profiles = profiles;
        this.educationRepo = educationRepo;
        this.employmentRepo = employmentRepo;
        this.familyRepo = familyRepo;
        this.photoRoot = Paths.get(photoDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.photoRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create profile photo storage", e);
        }
    }

    private String employeeId(Authentication auth) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        return auth.getName();
    }

    private EmployeeProfile profile(Authentication auth) {
        String employeeId = employeeId(auth);
        return profiles.findByUserId(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee profile not found."));
    }

    // ========================= EDUCATION =========================

    @GetMapping("/education")
    public List<EmployeeEducationRecord> education(Authentication auth) {
        return educationRepo.findByEmployeeIdOrderByEndDateDescIdDesc(employeeId(auth));
    }

    @PostMapping("/education")
    public EmployeeEducationRecord addEducation(Authentication auth, @RequestBody Map<String, Object> body) {
        String uid = employeeId(auth);
        EmployeeEducationRecord record = new EmployeeEducationRecord();
        record.setEmployeeId(uid);
        applyEducation(record, body);
        validateEducation(record);
        return educationRepo.save(record);
    }

    @PutMapping("/education/{id}")
    public EmployeeEducationRecord updateEducation(Authentication auth, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        EmployeeEducationRecord record = educationRepo.findByIdAndEmployeeId(id, employeeId(auth))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Education record not found."));
        applyEducation(record, body);
        validateEducation(record);
        return educationRepo.save(record);
    }

    @DeleteMapping("/education/{id}")
    public Map<String, Object> deleteEducation(Authentication auth, @PathVariable Long id) {
        EmployeeEducationRecord record = educationRepo.findByIdAndEmployeeId(id, employeeId(auth))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Education record not found."));
        educationRepo.delete(record);
        return Map.of("success", true);
    }

    // ====================== PREVIOUS EMPLOYMENT ==================

    @GetMapping("/employment-history")
    public List<EmployeeEmploymentHistory> employmentHistory(Authentication auth) {
        return employmentRepo.findByEmployeeIdOrderByEndDateDescIdDesc(employeeId(auth));
    }

    @PostMapping("/employment-history")
    public EmployeeEmploymentHistory addEmployment(Authentication auth, @RequestBody Map<String, Object> body) {
        String uid = employeeId(auth);
        EmployeeEmploymentHistory record = new EmployeeEmploymentHistory();
        record.setEmployeeId(uid);
        applyEmployment(record, body);
        validateEmployment(record);
        return employmentRepo.save(record);
    }

    @PutMapping("/employment-history/{id}")
    public EmployeeEmploymentHistory updateEmployment(Authentication auth, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        EmployeeEmploymentHistory record = employmentRepo.findByIdAndEmployeeId(id, employeeId(auth))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employment record not found."));
        applyEmployment(record, body);
        validateEmployment(record);
        return employmentRepo.save(record);
    }

    @DeleteMapping("/employment-history/{id}")
    public Map<String, Object> deleteEmployment(Authentication auth, @PathVariable Long id) {
        EmployeeEmploymentHistory record = employmentRepo.findByIdAndEmployeeId(id, employeeId(auth))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employment record not found."));
        employmentRepo.delete(record);
        return Map.of("success", true);
    }

    // =========================== FAMILY ==========================

    @GetMapping("/family")
    public List<EmployeeFamilyMember> family(Authentication auth) {
        return familyRepo.findByEmployeeIdOrderByIdAsc(employeeId(auth));
    }

    @PostMapping("/family")
    public EmployeeFamilyMember addFamilyMember(Authentication auth, @RequestBody Map<String, Object> body) {
        String uid = employeeId(auth);
        EmployeeFamilyMember record = new EmployeeFamilyMember();
        record.setEmployeeId(uid);
        applyFamily(record, body);
        validateFamily(record);
        return familyRepo.save(record);
    }

    @PutMapping("/family/{id}")
    public EmployeeFamilyMember updateFamilyMember(Authentication auth, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        EmployeeFamilyMember record = familyRepo.findByIdAndEmployeeId(id, employeeId(auth))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family member not found."));
        applyFamily(record, body);
        validateFamily(record);
        return familyRepo.save(record);
    }

    @DeleteMapping("/family/{id}")
    public Map<String, Object> deleteFamilyMember(Authentication auth, @PathVariable Long id) {
        EmployeeFamilyMember record = familyRepo.findByIdAndEmployeeId(id, employeeId(auth))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family member not found."));
        familyRepo.delete(record);
        return Map.of("success", true);
    }

    // ======================== PROFILE PHOTO ======================

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadPhoto(Authentication auth, @RequestParam("file") MultipartFile file) {
        EmployeeProfile profile = profile(auth);
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select a profile image.");
        }
        if (file.getSize() > 5L * 1024L * 1024L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile image must be 5 MB or smaller.");
        }

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("profile.jpg");
        String lower = original.toLowerCase(Locale.ROOT);
        String ext;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) ext = ".jpg";
        else if (lower.endsWith(".png")) ext = ".png";
        else if (lower.endsWith(".webp")) ext = ".webp";
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allowed profile image types: JPG, PNG, WEBP.");

        String stored = profile.getUserId().replaceAll("[^A-Za-z0-9_-]", "_") + "_" + UUID.randomUUID() + ext;
        Path target = photoRoot.resolve(stored).normalize();
        if (!target.startsWith(photoRoot)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid profile image filename.");
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            deleteOldPhoto(profile.getPhoto(), stored);
            profile.setPhoto(stored);
            profiles.save(profile);
            return Map.of("success", true, "photoAvailable", true);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to save profile image.", e);
        }
    }

    @GetMapping("/photo")
    public ResponseEntity<byte[]> photo(Authentication auth) {
        EmployeeProfile profile = profile(auth);
        if (profile.getPhoto() == null || profile.getPhoto().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        Path file = photoRoot.resolve(profile.getPhoto()).normalize();
        if (!file.startsWith(photoRoot) || !Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }
        try {
            String contentType = Optional.ofNullable(Files.probeContentType(file)).orElse(MediaType.IMAGE_JPEG_VALUE);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(CacheControl.noCache())
                    .body(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load profile image.", e);
        }
    }

    @DeleteMapping("/photo")
    public Map<String, Object> deletePhoto(Authentication auth) {
        EmployeeProfile profile = profile(auth);
        deleteOldPhoto(profile.getPhoto(), null);
        profile.setPhoto(null);
        profiles.save(profile);
        return Map.of("success", true, "photoAvailable", false);
    }

    private void deleteOldPhoto(String oldName, String keepName) {
        if (oldName == null || oldName.isBlank() || oldName.equals(keepName)) return;
        try {
            Path old = photoRoot.resolve(oldName).normalize();
            if (old.startsWith(photoRoot)) Files.deleteIfExists(old);
        } catch (IOException ignored) {
        }
    }

    // =========================== HELPERS =========================

    private void applyEducation(EmployeeEducationRecord r, Map<String, Object> b) {
        if (b.containsKey("level")) r.setLevel(textOrNull(b.get("level")));
        if (b.containsKey("institution")) r.setInstitution(textOrNull(b.get("institution")));
        if (b.containsKey("boardUniversity")) r.setBoardUniversity(textOrNull(b.get("boardUniversity")));
        if (b.containsKey("courseStream")) r.setCourseStream(textOrNull(b.get("courseStream")));
        if (b.containsKey("startDate")) r.setStartDate(date(b.get("startDate")));
        if (b.containsKey("endDate")) r.setEndDate(date(b.get("endDate")));
        if (b.containsKey("passingYear")) r.setPassingYear(textOrNull(b.get("passingYear")));
        if (b.containsKey("score")) r.setScore(textOrNull(b.get("score")));
        if (b.containsKey("certificateDocumentId")) r.setCertificateDocumentId(longValue(b.get("certificateDocumentId")));
    }

    private void validateEducation(EmployeeEducationRecord r) {
        if (r.getLevel() == null || r.getLevel().isBlank()) bad("Education level is required.");
        if (r.getInstitution() == null || r.getInstitution().isBlank()) bad("School/college/institution is required.");
        if (r.getStartDate() != null && r.getEndDate() != null && r.getEndDate().isBefore(r.getStartDate())) bad("Education end date cannot be before start date.");
    }

    private void applyEmployment(EmployeeEmploymentHistory r, Map<String, Object> b) {
        if (b.containsKey("companyName")) r.setCompanyName(textOrNull(b.get("companyName")));
        if (b.containsKey("designation")) r.setDesignation(textOrNull(b.get("designation")));
        if (b.containsKey("employmentType")) r.setEmploymentType(textOrNull(b.get("employmentType")));
        if (b.containsKey("location")) r.setLocation(textOrNull(b.get("location")));
        if (b.containsKey("startDate")) r.setStartDate(date(b.get("startDate")));
        if (b.containsKey("endDate")) r.setEndDate(date(b.get("endDate")));
        if (b.containsKey("lastDrawnSalary")) r.setLastDrawnSalary(decimal(b.get("lastDrawnSalary")));
        if (b.containsKey("reasonForLeaving")) r.setReasonForLeaving(textOrNull(b.get("reasonForLeaving")));
        if (b.containsKey("offerLetterDocumentId")) r.setOfferLetterDocumentId(longValue(b.get("offerLetterDocumentId")));
        if (b.containsKey("salarySlipDocumentId")) r.setSalarySlipDocumentId(longValue(b.get("salarySlipDocumentId")));
        if (b.containsKey("experienceLetterDocumentId")) r.setExperienceLetterDocumentId(longValue(b.get("experienceLetterDocumentId")));
    }

    private void validateEmployment(EmployeeEmploymentHistory r) {
        if (r.getCompanyName() == null || r.getCompanyName().isBlank()) bad("Company name is required.");
        if (r.getDesignation() == null || r.getDesignation().isBlank()) bad("Previous designation is required.");
        if (r.getStartDate() == null) bad("Employment start date is required.");
        if (r.getEndDate() != null && r.getEndDate().isBefore(r.getStartDate())) bad("Employment end date cannot be before start date.");
    }

    private void applyFamily(EmployeeFamilyMember r, Map<String, Object> b) {
        if (b.containsKey("fullName")) r.setFullName(textOrNull(b.get("fullName")));
        if (b.containsKey("relationship")) r.setRelationship(textOrNull(b.get("relationship")));
        if (b.containsKey("dob")) r.setDob(date(b.get("dob")));
        if (b.containsKey("phoneNumber")) r.setPhoneNumber(textOrNull(b.get("phoneNumber")));
        if (b.containsKey("occupation")) r.setOccupation(textOrNull(b.get("occupation")));
        if (b.containsKey("dependent")) r.setDependent(bool(b.get("dependent")));
        if (b.containsKey("emergencyContact")) r.setEmergencyContact(bool(b.get("emergencyContact")));
    }

    private void validateFamily(EmployeeFamilyMember r) {
        if (r.getFullName() == null || r.getFullName().isBlank()) bad("Family member name is required.");
        if (r.getRelationship() == null || r.getRelationship().isBlank()) bad("Relationship is required.");
    }

    private String textOrNull(Object value) {
        if (value == null) return null;
        String text = String.valueOf(value).trim();
        return text.isBlank() ? null : text;
    }

    private LocalDate date(Object value) {
        String valueText = textOrNull(value);
        if (valueText == null) return null;
        try {
            return LocalDate.parse(valueText);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must use YYYY-MM-DD format.");
        }
    }

    private Long longValue(Object value) {
        String valueText = textOrNull(value);
        if (valueText == null) return null;
        try {
            return Long.valueOf(valueText);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid document ID.");
        }
    }

    private BigDecimal decimal(Object value) {
        String valueText = textOrNull(value);
        if (valueText == null) return null;
        try {
            return new BigDecimal(valueText);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid salary amount.");
        }
    }

    private boolean bool(Object value) {
        if (value instanceof Boolean b) return b;
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private void bad(String message) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
