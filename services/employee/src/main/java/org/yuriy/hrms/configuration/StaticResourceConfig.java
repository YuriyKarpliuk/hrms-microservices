package org.yuriy.hrms.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(appProperties.getUpload().getAvatarDir());
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + uploadPath + "/");
        Path cvDir = Paths.get(appProperties.getUpload().getCvDir());
        String cvPath = cvDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/uploads/cv/**")
                .addResourceLocations("file:" + cvPath + "/");
    }
}
