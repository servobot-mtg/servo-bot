package com.ryan_mtg.servobot;

import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {
    private static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Value("${server.port}")
    private int port;

    @Value("${startup.database}")
    private boolean useDatabase;

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
            for (BotHome home : bot.getHomes()) {
                String website = String.format("http://localhost:%d/%s", port, home.getHomeName());
                LOGGER.info(String.format("Website link: %s", website));
            }
        }
    }

    @Autowired
    private CommitToDatabase committer;

    @PostConstruct
    public void startApplication() throws Exception {
        LOGGER.info("use database: " + useDatabase);
        bot.startBot();
        LOGGER.info("commit to database? " + shouldCommit);
        if (shouldCommit) {
            committer.commit();
        }
    }

    @Bean
    public boolean useDatabase() {
        return useDatabase;
    }

    public static boolean isTesting() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("win") >= 0;
    }
}
