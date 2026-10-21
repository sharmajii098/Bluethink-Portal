package EmployeeManagementSystem.service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/** RFC 5545 escaping, CRLF framing and UTF-8 octet-aware folding. */
public final class MeetingCalendarText {
    private MeetingCalendarText() {}
    public static String escape(String text) {
        return (text == null ? "" : text).replace("\\", "\\\\").replace("\r", "")
            .replace("\n", "\\n").replace(";", "\\;").replace(",", "\\,");
    }
    public static String fold(String line) {
        StringBuilder out = new StringBuilder(); int octets = 0;
        for (int i = 0; i < line.length();) {
            int cp = line.codePointAt(i); String next = new String(Character.toChars(cp));
            int n = next.getBytes(StandardCharsets.UTF_8).length;
            if (octets + n > 75) { out.append("\r\n "); octets = 1; }
            out.append(next); octets += n; i += Character.charCount(cp);
        }
        return out.append("\r\n").toString();
    }
    private static String date(OffsetDateTime d) {
        return d.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
    }
    public static String build(long id, String title, String agenda, String url, OffsetDateTime start,
          OffsetDateTime end, int sequence, boolean cancelled, String organizer, String recipient) {
        String method = recipient == null ? "PUBLISH" : cancelled ? "CANCEL" : "REQUEST";
        StringBuilder b = new StringBuilder("BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//BlueThink//Employee Portal//EN\r\nCALSCALE:GREGORIAN\r\nMETHOD:" + method + "\r\nBEGIN:VEVENT\r\n");
        b.append(fold("UID:bluethink-meeting-" + id + "@portal"));
        b.append(fold("DTSTAMP:" + date(OffsetDateTime.now())));
        b.append(fold("DTSTART:" + date(start))).append(fold("DTEND:" + date(end)));
        b.append(fold("SUMMARY:" + escape(title))).append(fold("DESCRIPTION:" + escape((agenda == null ? "" : agenda) + "\n" + url)));
        b.append(fold("URL:" + url.replace("\r", "").replace("\n", "")));
        b.append(fold("SEQUENCE:" + sequence)).append(fold("STATUS:" + (cancelled ? "CANCELLED" : "CONFIRMED")));
        if (organizer != null) b.append(fold("ORGANIZER:mailto:" + organizer));
        if (recipient != null) b.append(fold("ATTENDEE;RSVP=TRUE:mailto:" + recipient));
        return b.append("END:VEVENT\r\nEND:VCALENDAR\r\n").toString();
    }
}
