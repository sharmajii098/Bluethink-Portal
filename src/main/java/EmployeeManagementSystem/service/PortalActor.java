package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.PortalMeeting;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;

/** The server's authenticated identity, never a role or user ID supplied by a form. */
@Component
public class PortalActor {
    public Authentication authentication() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || a instanceof AnonymousAuthenticationToken)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to continue");
        return a;
    }
    public String id() { return authentication().getName(); }
    public boolean has(String... roles) {
        Set<String> allowed = Set.of(roles);
        return authentication().getAuthorities().stream()
            .anyMatch(a -> allowed.contains(a.getAuthority().replaceFirst("^ROLE_", "")));
    }
    public void hr() {
        if (!has("ADMIN", "HR")) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "HR or Admin permission required");
    }
    public void meetingEditor() {
        if (!has("ADMIN", "HR", "MANAGER")) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Meeting administrator permission required");
    }
    public void edit(PortalMeeting meeting) {
        meetingEditor();
        if (!has("ADMIN", "HR") && !id().equals(meeting.organizer))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Managers may change only meetings they organize");
    }
}
