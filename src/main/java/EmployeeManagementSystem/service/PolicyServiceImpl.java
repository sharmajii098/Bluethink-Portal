package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.Policy;
import EmployeeManagementSystem.entity.PolicyAcknowledgement;
import EmployeeManagementSystem.repository.PolicyAcknowledgementRepository;
import EmployeeManagementSystem.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyAcknowledgementRepository acknowledgementRepository;

    @Override
    public List<Map<String, Object>> listForUser(String userId) {

        LocalDate today = LocalDate.now();

        List<Map<String, Object>> out = new ArrayList<>();

        for (Policy p : policyRepository.findAllByOrderByEffectiveDateDesc()) {

            // Ignore policies that are not effective yet
            if (p.getEffectiveDate() != null
                    && p.getEffectiveDate().isAfter(today)) {
                continue;
            }

            Map<String, Object> row = new LinkedHashMap<>();

            row.put("id", p.getId());
            row.put("title", p.getTitle());
            row.put("description", p.getDescription());
            row.put("category", p.getCategory());
            row.put("documentPath", p.getDocumentPath());
            row.put("effectiveDate", p.getEffectiveDate());

            PolicyAcknowledgement acknowledgement =
                    acknowledgementRepository
                            .findByPolicyIdAndUserId(p.getId(), userId)
                            .orElse(null);

            row.put(
                    "acknowledged",
                    acknowledgement != null
                            && acknowledgement.isAcknowledged()
            );

            row.put(
                    "acknowledgedAt",
                    acknowledgement == null
                            ? null
                            : acknowledgement.getAcknowledgedAt()
            );

            row.put(
                    "acknowledgementCount",
                    acknowledgementRepository
                            .countByPolicyIdAndAcknowledgedTrue(p.getId())
            );

            out.add(row);
        }

        return out;
    }

    @Override
    public List<Policy> listAll() {
        return policyRepository.findAllByOrderByEffectiveDateDesc();
    }

    @Override
    public Policy get(Long id) {

        return policyRepository
                .findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException("Policy not found")
                );
    }

    @Override
    public Policy save(Policy p) {

        if (p == null) {
            throw new IllegalArgumentException("Policy is required");
        }

        if (p.getTitle() == null || p.getTitle().isBlank()) {
            throw new IllegalArgumentException("Policy title is required");
        }

        if (p.getEffectiveDate() == null) {
            p.setEffectiveDate(LocalDate.now());
        }

        return policyRepository.save(p);
    }

    @Override
    public void delete(Long id) {

        if (!policyRepository.existsById(id)) {
            throw new NoSuchElementException("Policy not found");
        }

        acknowledgementRepository.deleteByPolicyId(id);

        policyRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> acknowledge(Long id, String userId) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        Policy policy = get(id);

        PolicyAcknowledgement acknowledgement =
                acknowledgementRepository
                        .findByPolicyIdAndUserId(id, userId)
                        .orElseGet(PolicyAcknowledgement::new);

        acknowledgement.setPolicy(policy);
        acknowledgement.setUserId(userId);
        acknowledgement.setAcknowledged(true);
        acknowledgement.setAcknowledgedAt(LocalDateTime.now());

        acknowledgementRepository.save(acknowledgement);

        return Map.of(
                "success", true,
                "policyId", id,
                "acknowledged", true,
                "acknowledgedAt", acknowledgement.getAcknowledgedAt()
        );
    }
}