package com.experian.custodia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExperianCustodiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExperianCustodiaApplication.class, args);
	}

}
