package com.gestionst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class GestionStockProjectFApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionStockProjectFApplication.class, args);
	}
	@RestController
	@RequestMapping("/test")
	class HelloController {

	}

}
