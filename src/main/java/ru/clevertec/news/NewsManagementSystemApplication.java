package ru.clevertec.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class NewsManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsManagementSystemApplication.class, args);
	}

}
