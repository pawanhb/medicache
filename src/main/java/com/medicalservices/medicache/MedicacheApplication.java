package com.medicalservices.medicache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MedicacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicacheApplication.class, args);
	}

}
