package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
 private final AnnouncementRepository repo; private final AnnouncementReadRepository reads; private final EmployeeProfileRepository profiles;
 public AnnouncementServiceImpl(AnnouncementRepository r,AnnouncementReadRepository ar,EmployeeProfileRepository p){repo=r;reads=ar;profiles=p;}
 private boolean visible(Announcement a,String role,String dept){
  LocalDateTime now=LocalDateTime.now(); if(a.getPublishAt()!=null&&a.getPublishAt().isAfter(now)) return false; if(a.getExpiresAt()!=null&&!a.getExpiresAt().isAfter(now)) return false;
  if(a.getAudienceRole()!=null&&!a.getAudienceRole().isBlank()&&!"ALL".equalsIgnoreCase(a.getAudienceRole())&&!a.getAudienceRole().equalsIgnoreCase(role)) return false;
  return a.getAudienceDepartment()==null||a.getAudienceDepartment().isBlank()||"ALL".equalsIgnoreCase(a.getAudienceDepartment())||a.getAudienceDepartment().equalsIgnoreCase(dept);
 }
 public List<Map<String,Object>> listForUser(String userId,String role){
  String dept=profiles.findByUserId(userId).map(p->p.getDepartment()).orElse("");
  Map<Long,AnnouncementRead> state=reads.findByUserId(userId).stream().collect(Collectors.toMap(AnnouncementRead::getAnnouncementId,x->x,(x,y)->y));
  return repo.findAllByOrderByPublishAtDesc().stream().filter(a->visible(a,role,dept)).map(a->{Map<String,Object> m=new LinkedHashMap<>(); m.put("announcement",a); AnnouncementRead r=state.get(a.getId()); m.put("read",r!=null&&r.getReadAt()!=null); m.put("acknowledged",r!=null&&r.isAcknowledged()); return m;}).toList();
 }
 public List<Announcement> listAll(){return repo.findAllByOrderByPublishAtDesc();}
 public Announcement create(Announcement a,String userId){a.setId(null);a.setCreatedBy(userId);return repo.save(a);}
 public Announcement update(Long id,Announcement in){Announcement a=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Announcement not found."));a.setTitle(in.getTitle());a.setContent(in.getContent());a.setCategory(in.getCategory());a.setPriority(in.getPriority());a.setPublishAt(in.getPublishAt());a.setExpiresAt(in.getExpiresAt());a.setAudienceRole(in.getAudienceRole());a.setAudienceDepartment(in.getAudienceDepartment());return repo.save(a);}
 public void delete(Long id){if(!repo.existsById(id))throw new IllegalArgumentException("Announcement not found.");repo.deleteById(id);}
 public Map<String,Object> markRead(Long id,String userId,boolean acknowledge){if(!repo.existsById(id))throw new IllegalArgumentException("Announcement not found.");AnnouncementRead r=reads.findByAnnouncementIdAndUserId(id,userId).orElseGet(()->{AnnouncementRead x=new AnnouncementRead();x.setAnnouncementId(id);x.setUserId(userId);return x;});r.setReadAt(LocalDateTime.now());if(acknowledge)r.setAcknowledged(true);reads.save(r);return Map.of("success",true,"read",true,"acknowledged",r.isAcknowledged());}
}
