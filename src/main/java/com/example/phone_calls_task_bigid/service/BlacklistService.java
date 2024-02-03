package com.example.phone_calls_task_bigid.service;
import java.io.InputStream;
/**
 * Service interface for managing phone numbers in the blacklist.
 */
public interface BlacklistService {
    /**
     * Loads phone numbers from a CSV file into the blacklist.
     *
     * @param inputStream The input stream of the CSV file containing phone numbers.
     */
    void loadBlacklistFromCsv(InputStream inputStream);
}