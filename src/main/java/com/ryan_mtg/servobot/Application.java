package com.ryan_mtg.servobot;

import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.security.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SpringBootApplication
@Import(SecurityConfig.class)
public class Application {
    private static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Environment environment;

    @Autowired
    private BotRegistrar botRegistrar;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.setLogStartupInfo(true);
        application.run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printAddress(final ApplicationReadyEvent event) {
        if (isTesting()) {
            int port = Integer.parseInt(environment.getProperty("local.server.port"));
            String botSite = String.format("http://localhost:%d", port);
            LOGGER.info(String.format("Website link: %s", botSite));
            for(Bot bot : botRegistrar.getBots()) {
                for (BotHome home : bot.getHomes()) {
                    String homeSite = String.format("%s/home/%s", botSite, home.getName());
                    LOGGER.info(String.format("  - Home link: %s", homeSite));
                }
            }
        }
    }

    @PostConstruct
    public void startApplication() throws Exception {
        for(Bot bot : botRegistrar.getBots()) {
            bot.startBot();
        }
    }

    public static boolean isTesting() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win");
    }
}
