package EmployeeManagementSystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Shared Jackson configuration used by Spring MVC and services/controllers that
 * inject ObjectMapper directly.
 *
 * The project uses java.time types (LocalDate, LocalDateTime, etc.). A naked
 * ObjectMapper does not handle those types, so register the JSR-310 module
 * explicitly and serialize dates as ISO-8601 strings rather than timestamps.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
