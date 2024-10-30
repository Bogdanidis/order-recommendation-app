package com.example.order_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OrderAppApplication{

	public static void main(String[] args) {
		SpringApplication.run(OrderAppApplication.class, args);
	}


}
