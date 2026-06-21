package com.example.edu_base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EduBaseApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(EduBaseApplication.class, args);
		try {
			System.out.println("studentGroupService: " + context.getBean("studentGroupService"));
			System.out.println("studentGroupController: " + context.getBean("studentGroupController"));
		} catch (Exception e) {
			System.err.println("Bean not found: " + e.getMessage());
		}
	}

}
