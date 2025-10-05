package org.yuriy.department.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaDepartmentTopicConfig {
    @Bean
    public NewTopic departmentEventsTopic() {
        return TopicBuilder.name("department-topic").build();
    }
}
