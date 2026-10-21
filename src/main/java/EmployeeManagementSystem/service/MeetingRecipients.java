package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Component
public class MeetingRecipients {
    private final EmployeeRepository employees;
    private final PortalActor actor;
    private final Set<String> externalDomains;
    public MeetingRecipients(EmployeeRepository employees, PortalActor actor,
          @Value("${bluethink.meetings.external-recipient-domains:}") String domains) {
        this.employees = employees; this.actor = actor;
        this.externalDomains = new HashSet<>();
        for (String d : domains.split(",")) if (!d.isBlank()) externalDomains.add(d.trim().toLowerCase(Locale.ROOT));
    }
    public static String normalize(String input) {
        String v = input == null ? "" : input.trim().toLowerCase(Locale.ROOT);
        if (v.length() > 254 || !v.matches("^[a-z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-z0-9](?:[a-z0-9-]*[a-z0-9])?(?:\\.[a-z0-9](?:[a-z0-9-]*[a-z0-9])?)+$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid recipient email");
        return v;
    }
    public Set<String> validate(List<String> input) {
        if (input == null || input.isEmpty() || input.size() > 30)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide 1 to 30 recipients");
        Set<String> recipients = new LinkedHashSet<>();
        Employee manager = actor.has("ADMIN", "HR") ? null : employees.findByRegisterEmployee_UserId(actor.id())
            .or(() -> employees.findByEmployeeProfile_UserId(actor.id())).orElse(null);
        for (String raw : input) {
            String email = normalize(raw);
            Employee target = employees.findByEmailIgnoreCase(email).orElse(null);
            boolean permitted;
            if (actor.has("ADMIN", "HR"))
                permitted = target != null || externalDomains.contains(email.substring(email.indexOf('@') + 1));
            else permitted = manager != null && target != null &&
                (Objects.equals(target.getId(), manager.getId()) || (target.getReportingManager() != null &&
                 Objects.equals(target.getReportingManager().getId(), manager.getId())));
            if (!permitted) throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Recipients must be authorized employees; managers may invite themselves and direct reports only");
            recipients.add(email);
        }
        return recipients;
    }
}
