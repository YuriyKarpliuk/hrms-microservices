package org.yuriy.payrollservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaPayrollTopicConfig {
    @Bean
    public NewTopic payrollEventsTopic() {
        return TopicBuilder
                .name("payroll-events")
                .build();
    }
}
