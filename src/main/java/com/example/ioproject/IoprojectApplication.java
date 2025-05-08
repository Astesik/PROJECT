package com.example.ioproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class IoprojectApplication {

    public static void main(String[] args) {

        // Ładowanie zmiennych środowiskowych z pliku .env
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(IoprojectApplication.class, args);
    }

}
