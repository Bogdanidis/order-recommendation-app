package com.example.order_app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderAppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(OrderAppApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		System.out.println("im in");

	}
}
