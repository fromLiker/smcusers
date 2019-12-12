package com.ibm.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@ComponentScan(basePackages = "com.ibm.users.service.*")
//@EnableJpaRepositories(basePackages = "com.ibm.users.repository.*") 
//@EntityScan("com.ibm.users.entity.*")
@SpringBootApplication
public class UsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

}
