package com.example.phone_calls_task_bigid.service.impl;
import com.example.phone_calls_task_bigid.model.Contact;
import com.example.phone_calls_task_bigid.repository.ContactRepository;
import com.example.phone_calls_task_bigid.service.ContactService;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public void loadContactsFromCsv(InputStream inputStream) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            // Read CSV header
            String[] header = reader.readNext();

            if (header != null && header.length == 2 && "name".equalsIgnoreCase(header[0]) && "phone".equalsIgnoreCase(header[1])) {
                String[] line;

                // Check if the database is empty
                boolean isDatabaseEmpty = contactRepository.count() == 0;

                // Read CSV data and save contacts to the database if it's empty
                while ((line = reader.readNext()) != null) {
                    if (line.length == 2) {
                        String name = line[0];
                        String phoneNumber = line[1];

                        // If the database is empty or the contact doesn't exist, save it
                        if (isDatabaseEmpty || !contactRepository.existsByPhoneNumber(phoneNumber)) {
                            Contact contact = new Contact();
                            contact.setName(name);
                            contact.setPhoneNumber(phoneNumber);
                            contactRepository.save(contact);
                        }
                    }
                }
            } else {
                throw new RuntimeException("Invalid CSV file format. Check the header.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading contacts from CSV: " + e.getMessage(), e);
        }
    }
    @Override
    @Transactional
    public void updatePhoneNumber(String oldPhoneNumber, String newPhoneNumber) {
        // Perform the update in the contactRepository
        Optional<Contact> contactOptional = contactRepository.findByPhoneNumber(oldPhoneNumber);
        contactOptional.ifPresent(contact -> {
            contact.setPhoneNumber(newPhoneNumber);
            contactRepository.save(contact);
        });
    }
}
