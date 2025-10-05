package org.yuriy.hrms.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaEmployeeTopicConfig {
    @Bean
    public NewTopic employeeEventsTopic() {
        return TopicBuilder.name("employee-events").build();
    }
}
