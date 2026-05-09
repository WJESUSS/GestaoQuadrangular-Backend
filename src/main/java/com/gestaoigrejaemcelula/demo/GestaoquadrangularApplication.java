package com.gestaoigrejaemcelula.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GestaoquadrangularApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoquadrangularApplication.class, args);
	}


}
