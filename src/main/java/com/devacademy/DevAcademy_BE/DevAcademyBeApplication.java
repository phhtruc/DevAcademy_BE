package com.devacademy.DevAcademy_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class DevAcademyBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevAcademyBeApplication.class, args);
	}

}
