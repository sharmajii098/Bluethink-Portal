package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.Holiday;
import EmployeeManagementSystem.repository.HolidayRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
@Service
public class HolidayServiceImpl implements HolidayService {
 private final HolidayRepository repo; public HolidayServiceImpl(HolidayRepository r){repo=r;}
 public List<Holiday> upcoming(LocalDate from,LocalDate to){return repo.findByActiveTrueAndHolidayDateBetweenOrderByHolidayDateAsc(from,to);}
 public List<Holiday> all(){return repo.findAllByOrderByHolidayDateAsc();}
 private void validate(Holiday x){if(x.getHolidayDate()==null)throw new IllegalArgumentException("Holiday date is required.");if(x.getName()==null||x.getName().isBlank())throw new IllegalArgumentException("Holiday name is required.");if(x.getHolidayType()==null||!Set.of("PUBLIC","OPTIONAL","COMPANY").contains(x.getHolidayType().toUpperCase()))throw new IllegalArgumentException("Invalid holiday type.");if(x.getLocation()==null||x.getLocation().isBlank())x.setLocation("ALL");}
 public Holiday create(Holiday x){validate(x);if(repo.existsByHolidayDateAndNameIgnoreCaseAndLocationIgnoreCase(x.getHolidayDate(),x.getName(),x.getLocation()))throw new IllegalArgumentException("This holiday already exists for the selected location.");x.setId(null);return repo.save(x);}
 public Holiday update(Long id,Holiday x){Holiday e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Holiday not found."));validate(x);e.setHolidayDate(x.getHolidayDate());e.setName(x.getName());e.setDescription(x.getDescription());e.setHolidayType(x.getHolidayType().toUpperCase());e.setLocation(x.getLocation());e.setOptionalHoliday(Boolean.TRUE.equals(x.getOptionalHoliday()));e.setActive(Boolean.TRUE.equals(x.getActive()));return repo.save(e);}
 public void delete(Long id){repo.delete(repo.findById(id).orElseThrow(()->new NoSuchElementException("Holiday not found.")));}
}
