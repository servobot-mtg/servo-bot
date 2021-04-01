package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.Application;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
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
    private static final int HTTP_PORT = 5000;
    private static final int HTTPS_PORT = 443;
    private static final boolean USE_HTTPS = false;
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

        resourceHandlerRegistry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/images/");
    }

    @Component
    public static class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
        @Override
        public void customize(final ConfigurableWebServerFactory factory) {
            //TODO: remove when deployed with HTTPS
            // factory.setPort(HTTPS_PORT);
            if (USE_HTTPS && Application.isTesting()) {
                factory.setPort(HTTPS_PORT);
            } else {
                factory.setPort(HTTP_PORT);
            }
        }
    }

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        //TODO: remove when deployed with HTTPS
        if (Application.isTesting() && USE_HTTPS) {
            TomcatServletWebServerFactory tomcatFactory = new TomcatServletWebServerFactory() {
                @Override
                protected void postProcessContext(final Context context) {
                    SecurityConstraint securityConstraint = new SecurityConstraint();
                    securityConstraint.setUserConstraint("CONFIDENTIAL");
                    SecurityCollection securityCollection = new SecurityCollection();
                    securityCollection.addPattern("/*");
                    securityConstraint.addCollection(securityCollection);
                    context.addConstraint(securityConstraint);
                }
            };
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setScheme("http");
            connector.setPort(HTTP_PORT);
            connector.setRedirectPort(HTTPS_PORT);
            tomcatFactory.addAdditionalTomcatConnectors(connector);
            return tomcatFactory;
        } else {
            return new TomcatServletWebServerFactory();
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
