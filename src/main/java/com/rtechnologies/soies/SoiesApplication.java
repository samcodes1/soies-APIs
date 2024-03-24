package com.rtechnologies.soies;

import com.rtechnologies.soies.model.dto.CreateTeacherDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SpringBootApplication
public class SoiesApplication {
	public static void main(String[] args) {
		SpringApplication.run(SoiesApplication.class, args);
	}

	@PostMapping("/message")
	public ResponseEntity<?> dummy() {
		return ResponseEntity.status(200)
				.body("Doneee");
	}
}
