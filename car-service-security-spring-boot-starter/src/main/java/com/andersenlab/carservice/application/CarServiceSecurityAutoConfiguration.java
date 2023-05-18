package com.andersenlab.carservice.application;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@AutoConfiguration
@EnableMethodSecurity
class CarServiceSecurityAutoConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer().jwt().jwtAuthenticationConverter(new CarServiceJwtAuthenticationConverter())
                .and()
                .and()
                .build();
    }

    @Bean
    SecuredProxy securedProxy() {
        return new SecuredProxy();
    }
}
