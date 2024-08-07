package net.univwork.api;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// JVM 기본 시간대를 한국 시간대(KST)로 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
