package EmployeeManagementSystem.service;

import java.net.URI;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** Pure validation shared by scheduled meetings and daily stand-up configuration. */
public final class MeetingRules {
    private MeetingRules() {}
    public static String link(String value) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) return "";
        if (text.length() > 1000) throw new IllegalArgumentException("Meeting link is too long.");
        try {
            URI uri = URI.create(text);
            String host = uri.getHost();
            if (!"https".equalsIgnoreCase(uri.getScheme()) || host == null || uri.getUserInfo() != null
                    || host.indexOf('.') < 0 || host.matches("[0-9.]+") || host.contains(":"))
                throw new IllegalArgumentException();
            return uri.toASCIIString();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Use an HTTPS meeting link with a public hostname and no embedded credentials.");
        }
    }
    public static ZoneId zone(String value) {
        try { return ZoneId.of(value); }
        catch (Exception e) { throw new IllegalArgumentException("Select a valid IANA time zone."); }
    }
    public static Set<DayOfWeek> days(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Choose at least one working day.");
        Map<String, DayOfWeek> map = Map.of("MON",DayOfWeek.MONDAY,"TUE",DayOfWeek.TUESDAY,
            "WED",DayOfWeek.WEDNESDAY,"THU",DayOfWeek.THURSDAY,"FRI",DayOfWeek.FRIDAY,"SAT",DayOfWeek.SATURDAY,"SUN",DayOfWeek.SUNDAY);
        Set<DayOfWeek> result = new LinkedHashSet<>();
        for (String token : value.toUpperCase(Locale.ROOT).split(",", -1)) {
            DayOfWeek day = map.get(token.trim());
            if (day == null) throw new IllegalArgumentException("Working days must use MON,TUE,WED,THU,FRI,SAT,SUN.");
            result.add(day);
        }
        return result;
    }
    public static void schedule(LocalDateTime start, LocalDateTime end, String timezone, String recurrence, LocalDate until) {
        ZoneId z = zone(timezone);
        if (start == null || end == null || !end.isAfter(start) || Duration.between(start,end).compareTo(Duration.ofHours(24)) > 0)
            throw new IllegalArgumentException("Meeting end must be after start, within 24 hours.");
        if (z.getRules().getValidOffsets(start).size()!=1 || z.getRules().getValidOffsets(end).size()!=1)
            throw new IllegalArgumentException("Choose an unambiguous time outside a daylight-saving clock change.");
        if (!Set.of("NONE","DAILY","WEEKDAYS","WEEKLY").contains(recurrence))
            throw new IllegalArgumentException("Unsupported recurrence.");
        if (!"NONE".equals(recurrence) && (until==null || until.isBefore(start.toLocalDate()) || until.isAfter(start.toLocalDate().plusDays(366))))
            throw new IllegalArgumentException("Recurring meetings need an end date within one year of their start.");
    }
    public static boolean occurs(LocalDate original, LocalDate day, String recurrence, LocalDate until) {
        if (day.isBefore(original)) return false;
        if ("NONE".equals(recurrence)) return day.equals(original);
        if (until == null || day.isAfter(until)) return false;
        return switch (recurrence) {
            case "DAILY" -> true;
            case "WEEKDAYS" -> day.getDayOfWeek()!=DayOfWeek.SATURDAY && day.getDayOfWeek()!=DayOfWeek.SUNDAY;
            case "WEEKLY" -> ChronoUnit.DAYS.between(original,day)%7==0;
            default -> false;
        };
    }
}
