package org.yuriy.leaveservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaLeaveTopicConfig {
    @Bean
    public NewTopic leaveEventsTopic() {
        return TopicBuilder
                .name("leave-events")
                .build();
    }
}
