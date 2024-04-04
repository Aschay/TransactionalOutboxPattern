package com.aschay.TxOutboxDebzium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TxOutboxDebziumApplication {

	public static void main(String[] args) {
		SpringApplication.run(TxOutboxDebziumApplication.class, args);
	}

}
