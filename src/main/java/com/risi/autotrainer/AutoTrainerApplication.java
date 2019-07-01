package com.risi.autotrainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class AutoTrainerApplication {

    public static void main(String[] args) {
        Locale.setDefault(Locale.UK);
        SpringApplication.run(AutoTrainerApplication.class, args);
    }
}
