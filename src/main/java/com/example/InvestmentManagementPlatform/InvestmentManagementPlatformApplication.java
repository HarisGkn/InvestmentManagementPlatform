package com.example.InvestmentManagementPlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class InvestmentManagementPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestmentManagementPlatformApplication.class, args);
	}

}
