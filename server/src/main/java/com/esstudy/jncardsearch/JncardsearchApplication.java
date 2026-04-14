package com.esstudy.jncardsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//시큐리티 비활성화
@EnableJpaAuditing
@SpringBootApplication(exclude = {
SecurityAutoConfiguration.class
})
public class JncardsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(JncardsearchApplication.class, args);
	}

}
