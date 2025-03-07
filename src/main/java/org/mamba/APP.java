package org.mamba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class APP {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        SpringApplication.run(APP.class, args);
    }
}