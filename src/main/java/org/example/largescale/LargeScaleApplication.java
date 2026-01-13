package org.example.largescale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LargeScaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(LargeScaleApplication.class, args);
    }

}
