package org.yuriy.timesheetservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTimesheetTopicConfig {
    @Bean
    public NewTopic timesheetEventsTopic() {
        return TopicBuilder
                .name("timesheet-events")
                .build();
    }
}
