package org.yuriy.organization.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaOrganizationTopicConfig {
    @Bean
    public NewTopic organizationEventsTopic() {
        return TopicBuilder
                .name("organization-events")
                .build();
    }
}
