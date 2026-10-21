package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.EmployeeAsset;
import EmployeeManagementSystem.repository.EmployeeAssetRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmployeeAssetServiceImpl implements EmployeeAssetService {
    private final EmployeeAssetRepository repo;
    public EmployeeAssetServiceImpl(EmployeeAssetRepository repo){ this.repo=repo; }
    public List<EmployeeAsset> my(String userId){ return repo.findByAssignedUserIdOrderByUpdatedAtDesc(userId); }
    public List<EmployeeAsset> all(){ return repo.findAllByOrderByUpdatedAtDesc(); }
    public EmployeeAsset create(EmployeeAsset a){ validate(a); a.setId(null); if(a.getAssetStatus()==null) a.setAssetStatus("AVAILABLE"); return repo.save(a); }
    public EmployeeAsset update(Long id, EmployeeAsset x){
        EmployeeAsset a=get(id); validate(x);
        a.setAssetTag(x.getAssetTag()); a.setAssetName(x.getAssetName()); a.setAssetType(x.getAssetType());
        a.setBrand(x.getBrand()); a.setModel(x.getModel()); a.setSerialNumber(x.getSerialNumber());
        a.setPurchaseDate(x.getPurchaseDate()); a.setWarrantyExpiry(x.getWarrantyExpiry()); a.setConditionStatus(x.getConditionStatus());
        a.setLocation(x.getLocation()); a.setNotes(x.getNotes()); return repo.save(a);
    }
    public EmployeeAsset assign(Long id,String userId,String name){
        EmployeeAsset a=get(id); if("MAINTENANCE".equals(a.getAssetStatus())) throw new IllegalArgumentException("Asset is under maintenance.");
        if(a.getAssignedUserId()!=null && !a.getAssignedUserId().isBlank()) throw new IllegalArgumentException("Asset is already assigned.");
        a.setAssignedUserId(userId); a.setAssignedEmployeeName(name); a.setAssignedAt(LocalDateTime.now()); a.setReturnedAt(null); a.setAssetStatus("ASSIGNED"); return repo.save(a);
    }
    public EmployeeAsset returnAsset(Long id){ EmployeeAsset a=get(id); a.setAssignedUserId(null); a.setAssignedEmployeeName(null); a.setReturnedAt(LocalDateTime.now()); a.setAssetStatus("AVAILABLE"); return repo.save(a); }
    public EmployeeAsset markMaintenance(Long id){ EmployeeAsset a=get(id); if(a.getAssignedUserId()!=null) throw new IllegalArgumentException("Return the asset before maintenance."); a.setAssetStatus("MAINTENANCE"); return repo.save(a); }
    public EmployeeAsset markAvailable(Long id){ EmployeeAsset a=get(id); a.setAssetStatus("AVAILABLE"); return repo.save(a); }
    public void delete(Long id){ EmployeeAsset a=get(id); if(a.getAssignedUserId()!=null) throw new IllegalArgumentException("Assigned assets cannot be deleted. Return the asset first."); repo.delete(a); }
    private void validate(EmployeeAsset a){ if(a.getAssetTag()==null||a.getAssetTag().isBlank()) throw new IllegalArgumentException("Asset tag is required."); if(a.getAssetName()==null||a.getAssetName().isBlank()) throw new IllegalArgumentException("Asset name is required."); if(a.getAssetType()==null||a.getAssetType().isBlank()) throw new IllegalArgumentException("Asset type is required."); if(a.getPurchaseDate()!=null&&a.getWarrantyExpiry()!=null&&a.getWarrantyExpiry().isBefore(a.getPurchaseDate())) throw new IllegalArgumentException("Warranty expiry cannot be before purchase date."); }
    private EmployeeAsset get(Long id){ return repo.findById(id).orElseThrow(()->new NoSuchElementException("Asset not found.")); }
}
