package com.eduardomarinho.cnpjutils;

import org.springframework.boot.SpringApplication;

public class TestCnpjutilsApplication {

	public static void main(String[] args) {
		SpringApplication.from(CnpjutilsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
