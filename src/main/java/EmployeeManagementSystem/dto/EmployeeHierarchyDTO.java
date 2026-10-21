package EmployeeManagementSystem.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class EmployeeHierarchyDTO {
    private Long id;
    private String userId;
    private String name;
    private String designation;
    private String role;
    private Long departmentId;
    private String departmentName;
    private Long reportingManagerId;
    private List<EmployeeHierarchyDTO> children = new ArrayList<>();

    public EmployeeHierarchyDTO() {}
    public EmployeeHierarchyDTO(Long id, String userId, String name, String designation, String role,
                                Long departmentId, String departmentName, Long reportingManagerId) {
        this.id=id; this.userId=userId; this.name=name; this.designation=designation; this.role=role;
        this.departmentId=departmentId; this.departmentName=departmentName; this.reportingManagerId=reportingManagerId;
    }
    public void addChild(EmployeeHierarchyDTO child) { getChildren().add(child); }
    public List<EmployeeHierarchyDTO> getChildren() { if(children==null) children=new ArrayList<>(); return children; }
}
