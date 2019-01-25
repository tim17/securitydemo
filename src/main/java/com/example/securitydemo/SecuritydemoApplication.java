package com.example.securitydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SecuritydemoApplication extends SpringBootServletInitializer {


	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SecuritydemoApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SecuritydemoApplication.class, args);
	}
	//    mvn clean spring-boot:run -Dmaven.test.skip=true -Dspring-boot.run.profiles=dev -Djvm_port=8080
	//    mvn clean package -Dmaven.test.skip=true -Dspring-boot.run.profiles=dev -Djvm_port=8080

}

