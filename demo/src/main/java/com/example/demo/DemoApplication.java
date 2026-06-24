package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * Spring Boot应用程序主入口类，负责启动整个Web应用。
 */
@SpringBootApplication
@MapperScan("com.mapper")
@ComponentScan({"com.controller", "com.config", "com.service", "com.javaBean"})
public class DemoApplication {

	/**
	 * 应用程序入口方法。
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
