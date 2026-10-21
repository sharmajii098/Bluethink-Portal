package EmployeeManagementSystem.controller;
import EmployeeManagementSystem.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.*;
@RestController @RequestMapping("/api/calendar-integrations")
public class CalendarIntegrationController {
 private final CalendarConnectionService connections;private final CalendarSyncService sync;
 public CalendarIntegrationController(CalendarConnectionService connections,CalendarSyncService sync){this.connections=connections;this.sync=sync;}
 @GetMapping public List<CalendarConnectionService.Status> status(){return connections.status();}
 @PostMapping("/{provider}/connect") public ResponseEntity<Map<String,String>> connect(@PathVariable String provider){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(connections.begin(provider));}
 public record Callback(String state,String code){}
 @PostMapping("/{provider}/complete") public CalendarConnectionService.Status complete(@PathVariable String provider,@RequestBody Callback callback){return connections.complete(provider,callback.state(),callback.code());}
 @DeleteMapping("/{provider}") @ResponseStatus(HttpStatus.NO_CONTENT) public void disconnect(@PathVariable String provider){connections.disconnect(provider);}
 @GetMapping("/events") public List<CalendarSyncService.LinkStatus> events(){return sync.list();}
 @PostMapping("/{provider}/meetings/{meetingId}") public CalendarSyncService.LinkStatus select(@PathVariable String provider,@PathVariable Long meetingId){return sync.select(provider,meetingId);}
 @DeleteMapping("/events/{id}") @ResponseStatus(HttpStatus.ACCEPTED) public void unselect(@PathVariable Long id){sync.unselect(id);}
 @PostMapping("/events/{id}/retry") @ResponseStatus(HttpStatus.NO_CONTENT) public void retry(@PathVariable Long id){sync.retry(id);}
}
