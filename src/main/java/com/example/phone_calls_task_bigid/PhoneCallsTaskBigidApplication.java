package com.example.phone_calls_task_bigid;

import com.example.phone_calls_task_bigid.service.ContactService;
import com.example.phone_calls_task_bigid.service.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.InputStream;

@SpringBootApplication
public class PhoneCallsTaskBigidApplication {

    @Autowired
    private ContactService contactService;

    @Autowired
    private BlacklistService blacklistService;

    public static void main(String[] args) {
        SpringApplication.run(PhoneCallsTaskBigidApplication.class, args);
    }

    @Bean
    CommandLineRunner loadContactData() {
        return args -> {
            try (InputStream inputStream = getClass().getResourceAsStream("/contactList.csv")) {
                contactService.loadContactsFromCsv(inputStream);
            } catch (Exception e) {
                throw new RuntimeException("Error loading contact data from CSV: " + e.getMessage(), e);
            }
        };
    }

    @Bean
    CommandLineRunner loadBlacklistData() {
        return args -> {
            try (InputStream inputStream = getClass().getResourceAsStream("/blackList.csv")) {
                blacklistService.loadBlacklistFromCsv(inputStream);
            } catch (Exception e) {
                throw new RuntimeException("Error loading blacklist data from CSV: " + e.getMessage(), e);
            }
        };
    }
}
