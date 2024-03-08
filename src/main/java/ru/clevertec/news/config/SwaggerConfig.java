package ru.clevertec.news.config;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenApiCustomizer sortTagsAlphabetically() {
        return openApi -> openApi.setTags(openApi.getTags()
                .stream()
                .sorted(Comparator.comparing(tag -> StringUtils.stripAccents(tag.getName())))
                .toList());
    }
}
