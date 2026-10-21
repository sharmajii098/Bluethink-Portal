package EmployeeManagementSystem.service;

import EmployeeManagementSystem.dto.EmployeeHierarchyDTO;
import EmployeeManagementSystem.entity.Department;
import EmployeeManagementSystem.entity.RegisterEmployee;
import EmployeeManagementSystem.repository.DepartmentRepository;
import EmployeeManagementSystem.repository.RegisterEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeHierarchyService {

    private final RegisterEmployeeRepository registerEmployeeRepository;
    private final DepartmentRepository departmentRepository;

    public List<EmployeeHierarchyDTO> getEmployeeHierarchy() {

        List<RegisterEmployee> employees = registerEmployeeRepository.findAll();

        if (employees == null || employees.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, String> departments = departmentRepository.findAll()
                .stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(
                        Department::getId,
                        Department::getDepartmentName,
                        (a, b) -> a
                ));

        Map<Long, EmployeeHierarchyDTO> map = new HashMap<>();

        for (RegisterEmployee e : employees) {

            if (e.getId() == null) {
                continue;
            }

            map.put(
                    e.getId(),
                    new EmployeeHierarchyDTO(
                            e.getId(),
                            e.getUserId(),
                            e.getName(),
                            e.getDesignation(),
                            e.getRole(),
                            e.getDepartmentId(),
                            departments.get(e.getDepartmentId()),
                            e.getReportingManagerId()
                    )
            );
        }

        List<EmployeeHierarchyDTO> roots = new ArrayList<>();

        for (RegisterEmployee e : employees) {

            if (e.getId() == null) {
                continue;
            }

            EmployeeHierarchyDTO current = map.get(e.getId());

            Long managerId = e.getReportingManagerId();

            EmployeeHierarchyDTO manager =
                    managerId == null ? null : map.get(managerId);

            if (manager == null || Objects.equals(managerId, e.getId())) {
                roots.add(current);
            } else {
                manager.addChild(current);
            }
        }

        Comparator<EmployeeHierarchyDTO> order =
                Comparator.comparing(
                        x -> Optional.ofNullable(x.getName()).orElse(""),
                        String.CASE_INSENSITIVE_ORDER
                );

        Consumer<EmployeeHierarchyDTO> sortTree =
                new Consumer<>() {
                    @Override
                    public void accept(EmployeeHierarchyDTO n) {

                        n.getChildren().sort(order);

                        n.getChildren().forEach(this);
                    }
                };

        roots.sort(order);
        roots.forEach(sortTree);

        return roots;
    }
}