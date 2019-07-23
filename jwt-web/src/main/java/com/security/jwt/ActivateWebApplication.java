package com.security.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.security.jwt.mapper")
public class ActivateWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivateWebApplication.class, args);
	}

}
