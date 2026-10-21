package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.TaxDeclaration;
import java.util.*;
public interface TaxDeclarationService{
 List<TaxDeclaration> my(String userId); List<TaxDeclaration> all(); TaxDeclaration create(TaxDeclaration x,String userId,String name); TaxDeclaration submit(Long id,String userId); TaxDeclaration review(Long id,String status,String comment,String reviewer);
}
