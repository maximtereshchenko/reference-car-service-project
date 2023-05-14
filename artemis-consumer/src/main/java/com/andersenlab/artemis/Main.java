package com.andersenlab.artemis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJms
class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }

    @Bean
    Consumer consumer() {
        return new Consumer();
    }
}
