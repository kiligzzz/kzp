package com.kiligz.kzp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class KzpUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(KzpUserApplication.class, args);
    }

}
