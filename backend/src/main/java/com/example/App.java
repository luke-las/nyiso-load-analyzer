package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner importDataOnStartup() {
        return args -> {
            try {
                System.out.println("Running LoadDataImporter on startup...");

                LocalDateTime startDate = null;

                LocalDateTime endDate = null;
                for (String arg : args) {
                    if (arg.startsWith("--start=")) {
                        startDate = LocalDateTime.parse(arg.substring("--start=".length()));
                        System.out.println("startDate:" +startDate);

                    } else if (arg.startsWith("--end=")) {
                        endDate = LocalDateTime.parse(arg.substring("--end=".length()));
                    }
                }

                new LoadDataImporter().importDirectory(
                    Paths.get("..", "scripts", "downloaded_files"), startDate, endDate);

                System.out.println("LoadDataImporter completed successfully.");
            } catch (Exception e) {
                e.printStackTrace();  
                throw e;  // rethrow to make Spring aware
            }
        };
    }
}
