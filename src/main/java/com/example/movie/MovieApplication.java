package com.example.movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
        exclude = {SecurityAutoConfiguration.class},
        scanBasePackages = "com.example.movie"
)
@EnableCaching
public class MovieApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieApplication.class, args);
    }

}
