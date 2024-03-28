package ru.clevertec.news.config;

import jakarta.ws.rs.core.UriBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.clevertec.exceptionhandlerstarter.handler.NewsManagementSystemExceptionHandler;

@Configuration
public class AppConfig {

    @Bean
    @LoadBalanced
    @Profile("!test")
    public WebClient.Builder webClientBuilderCommentsUrl() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl("http://comment-service");
    }

    @Bean
    @LoadBalanced
    @Profile("!test")
    public WebClient.Builder webClientBuilderNewsUrl() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl("http://news-service");
    }

    @Bean
    @Profile("prod")
    public NewsManagementSystemExceptionHandler handler(){
        return new NewsManagementSystemExceptionHandler();
    }
}
