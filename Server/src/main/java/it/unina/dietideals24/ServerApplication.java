package it.unina.dietideals24;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.logging.Logger;

@SpringBootApplication
public class ServerApplication {
    static Logger logger = Logger.getLogger(ServerApplication.class.getName());

    public static void main(String[] args) {
        createNecessaryDirectories();
        SpringApplication.run(ServerApplication.class, args);
    }

    private static void createNecessaryDirectories() {
        if (new File("images").mkdir())
            logger.info("images folder created");
        else
            logger.info("images folder already exists, skipping...");

        if (new File("images/user").mkdir())
            logger.info("images/user folder created");
        else
            logger.info("images/user folder already exists, skipping...");

        if (new File("images/english_auction").mkdir())
            logger.info("images/english_auction folder created");
        else
            logger.info("images/english_auction folder already exists, skipping...");

        if (new File("images/downward_auction").mkdir())
            logger.info("images/downward_auction folder created");
        else
            logger.info("images/downward_auction folder already exists, skipping...");
    }
}
