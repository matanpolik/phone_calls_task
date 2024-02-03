package com.example.phone_calls_task_bigid.service.impl;
import com.example.phone_calls_task_bigid.model.Blacklist;
import com.example.phone_calls_task_bigid.repository.BlacklistRepository;
import com.example.phone_calls_task_bigid.service.BlacklistService;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class BlacklistServiceImpl implements BlacklistService {

    private final BlacklistRepository blacklistRepository;

    public BlacklistServiceImpl(BlacklistRepository blacklistRepository) {
        this.blacklistRepository = blacklistRepository;
    }

    @Override
    @Transactional
    public void loadBlacklistFromCsv(InputStream inputStream) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            // Read CSV header
            String[] header = reader.readNext();

            if (header != null && header.length == 1 && "phone".equalsIgnoreCase(header[0])) {
                String[] line;

                // Read CSV data and save blacklisted numbers to the database
                while ((line = reader.readNext()) != null) {
                    if (line.length == 1) {
                        String phoneNumber = line[0];

                        // Check if the phone number is not already in the blacklist
                        if (!blacklistRepository.existsById(phoneNumber)) {
                            Blacklist blacklist = new Blacklist(phoneNumber);
                            blacklistRepository.save(blacklist);
                        }
                    }
                }
            } else {
                throw new RuntimeException("Invalid CSV file format. Check the header.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading blacklist from CSV: " + e.getMessage(), e);
        }
    }
}
