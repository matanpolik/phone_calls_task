package com.example.phone_calls_task_bigid.service.impl;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.phone_calls_task_bigid.model.Contact;
import com.example.phone_calls_task_bigid.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@SpringBootTest
public class ContactServiceImplTest {
    @Mock
    private ContactRepository contactRepository;
    @InjectMocks
    private ContactServiceImpl contactService;
    @Captor
    private ArgumentCaptor<Contact> contactArgumentCaptor;
    @Test
    void loadContactsFromCsv_ValidCsv_AddsContacts() {
        // Arrange
        String csvData = "name,phone\nMatan Polik,123456789\nPolik Matan,987654321";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        when(contactRepository.count()).thenReturn(0L); // Assuming database is initially empty
        // Act
        contactService.loadContactsFromCsv(inputStream);
        // Assert
        verify(contactRepository, times(2)).save(any(Contact.class));
    }

    @Test
    void loadContactsFromCsv_InvalidCsv_ThrowsException() {
        //Arrange
        String csvData = "invalid_header\nMatan Polik,123456789";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        //Act
        assertThrows(RuntimeException.class, () -> contactService.loadContactsFromCsv(inputStream));
        //Assert
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void updatePhoneNumber_ValidUpdate_UpdatesContact() {
        // Arrange
        String oldPhoneNumber = "123456789";
        String newPhoneNumber = "987654321";
        Contact contact = new Contact();
        contact.setName("Matan Polik");
        contact.setPhoneNumber(oldPhoneNumber);

        when(contactRepository.findByPhoneNumber(oldPhoneNumber)).thenReturn(Optional.of(contact));
        // Act
        contactService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber);
        // Assert
        verify(contactRepository).save(contactArgumentCaptor.capture());
        assertEquals(newPhoneNumber, contactArgumentCaptor.getValue().getPhoneNumber());
    }
}