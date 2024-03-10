package io.github.nerfi58.bookster.config;

import jakarta.validation.ClockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    @Bean
    public ClockProvider clockProvider() {
        return Clock::systemUTC;
    }
}
