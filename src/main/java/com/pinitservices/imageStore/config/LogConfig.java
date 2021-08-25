package com.pinitservices.imageStore.config;

import java.util.logging.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LogConfig {

    @Bean
    @Scope("prototype")
    public Logger getLogger(InjectionPoint ip) {
        return Logger.getLogger(ip.getDeclaredType().getName());
    }

}
