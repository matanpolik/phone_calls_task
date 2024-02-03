package com.example.phone_calls_task_bigid.service;
import com.example.phone_calls_task_bigid.model.Contact;
import java.io.InputStream;
import java.util.List;
/**
 * Service interface for managing contacts.
 */
public interface ContactService {
    /**
     * Loads contacts from a CSV file into the database.
     *
     * @param inputStream The input stream of the CSV file containing contact information.
     */
    void loadContactsFromCsv(InputStream inputStream);

    /**
     * Updates the phone number of an existing contact.
     *
     * @param oldPhoneNumber The current phone number of the contact to be updated.
     * @param newPhoneNumber The new phone number to replace the old one.
     */
    void updatePhoneNumber(String oldPhoneNumber, String newPhoneNumber);
}
