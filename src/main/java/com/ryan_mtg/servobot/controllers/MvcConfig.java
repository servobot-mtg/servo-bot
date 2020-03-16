package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.JdbcHttpSessionConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private static final String RESOURCE_DIRECTORY = "src/main/resources";

    @Bean
    public SpringResourceTemplateResolver springTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setOrder(1);

        // While testing, load from source
        if (Application.isTesting()) {
            templateResolver.setPrefix(String.format("file:%s/templates/", RESOURCE_DIRECTORY));
            templateResolver.setCacheable(false);
        } else {
            templateResolver.setPrefix("classpath:templates/");
        }

        return templateResolver;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry resourceHandlerRegistry) {
        ResourceHandlerRegistration registration = resourceHandlerRegistry.addResourceHandler("/**");
        if (Application.isTesting()) {
            registration.addResourceLocations(String.format("file:%s/static/", RESOURCE_DIRECTORY));
            registration.setCachePeriod(0);
        } else {
            registration.addResourceLocations("classpath:/static/");
        }
    }

    @Component
    public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
        @Override
        public void customize(final ConfigurableWebServerFactory factory) {
            //AWS Elastic Beanstalk port
            factory.setPort(5000);
        }
    }

    @Configuration
    static class HttpSessionConfiguration extends JdbcHttpSessionConfiguration {
        @Autowired
        void customize(final SessionProperties sessionProperties) {
            Duration timeout = sessionProperties.getTimeout();
            if (timeout != null) {
                setMaxInactiveIntervalInSeconds((int) timeout.getSeconds());
            }
            setTableName("session");
        }
    }
}
