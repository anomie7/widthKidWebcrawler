package com.crawling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@EntityScan(
        basePackageClasses = {WebCrawlingPracticeApplication.class, Jsr310JpaConverters.class}
)
@SpringBootApplication
public class WebCrawlingPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebCrawlingPracticeApplication.class, args);
	}
}
