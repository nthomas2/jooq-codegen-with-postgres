package com.nthomas.jooqcodegenwithpostgres;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
@SpringBootApplication
@EnableSwagger2
public class JooqCodegenWithPostgresApplication {

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
					.paths(not(equalTo("/error"))).build()
				.genericModelSubstitutes(ResponseEntity.class)
				.enableUrlTemplating(true)
				.tags(new Tag("User", "User Manipulation"));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}

	public static void main(String[] args) {
		SpringApplication.run(JooqCodegenWithPostgresApplication.class, args);
	}

}
