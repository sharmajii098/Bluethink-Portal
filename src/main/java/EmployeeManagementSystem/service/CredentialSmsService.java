package EmployeeManagementSystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/** Optional Twilio SMS transport. No SMS is claimed unless Twilio accepts the request. */
@Service
public class CredentialSmsService {
    private final String sid;
    private final String token;
    private final String from;
    public CredentialSmsService(@Value("${hrms.sms.twilio.sid:}") String sid,
                                @Value("${hrms.sms.twilio.token:}") String token,
                                @Value("${hrms.sms.twilio.from:}") String from) {
        this.sid = sid; this.token = token; this.from = from;
    }
    public boolean sendCredentials(String phone, String userId, String password) {
        if (sid.isBlank() || token.isBlank() || from.isBlank() || phone == null || phone.isBlank()) return false;
        String destination = phone.replaceAll("[\\s()-]", "");
        if (destination.matches("[0-9]{10}")) destination = "+91" + destination;
        else if (destination.matches("[1-9][0-9]{10,14}")) destination = "+" + destination;
        if (!destination.matches("\\+[1-9][0-9]{7,14}")) return false;
        try {
            String body = "Bluethink HRMS login ID: " + userId + " temporary password: " + password
                    + ". Please change your password after login.";
            String payload = "To=" + encode(destination) + "&From=" + encode(from) + "&Body=" + encode(body);
            String basic = Base64.getEncoder().encodeToString((sid + ":" + token).getBytes(StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.twilio.com/2010-04-01/Accounts/" + sid + "/Messages.json"))
                    .timeout(Duration.ofSeconds(8))
                    .header("Authorization", "Basic " + basic)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(payload)).build();
            int code = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()
                    .send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
            return code >= 200 && code < 300; // provider accepted, not handset-delivered
        } catch (Exception ex) {
            System.err.println("Credential SMS provider request failed: " + ex.getClass().getSimpleName());
            return false;
        }
    }
    private String encode(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
}
