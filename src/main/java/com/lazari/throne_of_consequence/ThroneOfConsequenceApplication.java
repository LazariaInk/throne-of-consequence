package com.lazari.throne_of_consequence;

import com.lazari.throne_of_consequence.config.OllamaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OllamaProperties.class)
public class ThroneOfConsequenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThroneOfConsequenceApplication.class, args);
		System.out.println("Hello");
	}

}
