package org.yuriy.hrms.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String baseUrl;
    private Upload upload = new Upload();

    @Getter
    @Setter
    public static class Upload {
        private String avatarDir;
        private String cvDir;
    }
}
