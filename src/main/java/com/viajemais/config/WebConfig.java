// src/main/java/com/viajemais/config/WebConfig.java
package com.viajemais.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final StrictDoubleFormatter strictDoubleFormatter;

    public WebConfig(StrictDoubleFormatter strictDoubleFormatter) {
        this.strictDoubleFormatter = strictDoubleFormatter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(strictDoubleFormatter);
    }
}
