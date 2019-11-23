package com.ryan_mtg.servobot;

import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.security.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Import(SecurityConfig.class)
public class Application {
    private static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Value("${server.port}")
    private int port;

    @Value("${startup.commit}")
    private boolean shouldCommit;

    @Autowired
    @Qualifier("bot")
    private Bot bot;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.setLogStartupInfo(false);
        application.run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printAddress(final ApplicationReadyEvent event) {
        if (isTesting()) {
            String botSite = String.format("http://localhost:%d", port);
            LOGGER.info(String.format("Website link: %s", botSite));
            for (BotHome home : bot.getHomes()) {
                String homeSite = String.format("%s/home/%d", botSite, home.getId());
                LOGGER.info(String.format("  - Home link: %s", homeSite));
            }
        }
    }

    @Autowired
    private CommitToDatabase committer;

    @PostConstruct
    public void startApplication() throws Exception {
        bot.startBot();
        LOGGER.info("commit to database? " + shouldCommit);
        if (shouldCommit) {
            committer.commit();
        }
    }

    public static boolean isTesting() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("win") >= 0;
    }
}
