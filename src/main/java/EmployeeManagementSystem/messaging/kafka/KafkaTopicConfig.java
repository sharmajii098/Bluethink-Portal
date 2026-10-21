package EmployeeManagementSystem.messaging.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String EMPLOYEE_CREATED_TOPIC =
            "employee-created-topic";

    @Bean
    public NewTopic employeeCreatedTopic() {

        return new NewTopic(
                EMPLOYEE_CREATED_TOPIC,
                1,
                (short) 1
        );
    }
}