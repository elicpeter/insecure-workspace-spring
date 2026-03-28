package com.example.insecurecollab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InsecureCollabApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsecureCollabApplication.class, args);
    }
}
