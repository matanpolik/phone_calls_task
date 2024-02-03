package com.example.phone_calls_task_bigid.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.phone_calls_task_bigid.exception.BlockedPhoneNumberException;
import com.example.phone_calls_task_bigid.model.Blacklist;
import com.example.phone_calls_task_bigid.model.Contact;
import com.example.phone_calls_task_bigid.model.PhoneCall;
import com.example.phone_calls_task_bigid.repository.BlacklistRepository;
import com.example.phone_calls_task_bigid.repository.ContactRepository;
import com.example.phone_calls_task_bigid.repository.PhoneCallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class PhoneCallServiceImplTest {
    @Mock
    private PhoneCallRepository phoneCallRepository;
    @Mock
    private BlacklistRepository blacklistRepository;
    @Mock
    private ContactRepository contactRepository;
    @InjectMocks
    private PhoneCallServiceImpl phoneCallService;
    private PhoneCall phoneCall1;
    private PhoneCall phoneCall2;
    private PhoneCall phoneCall3;
    private PhoneCall phoneCall4;

    @BeforeEach
    void setUp() {
        // Common setup for all tests
        phoneCall1 = new PhoneCall("08-10-2021 09:10:30", "Outgoing", "179", "0518617755", true);
        phoneCall2 = new PhoneCall("09-10-2021 10:15:00", "Incoming", "120", "0518617756", false); // Different number
        phoneCall3 = new PhoneCall("10-10-2021 11:20:30", "Outgoing", "200", "0518617755", true); // Same number as `phoneCall`
        phoneCall4 = new PhoneCall("11-10-2021 12:30:45", "Incoming", "90", "0560265187", false); // Blacklisted number
    }

    @Test
    void GivenNumberNotInContacts_WhenAddPhoneCall_ThenPhoneCallAdded() {
        // Arrange
        when(contactRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());

        // Act
        phoneCallService.savePhoneCall(phoneCall1);

        // Assert
        verify(phoneCallRepository, times(1)).save(phoneCall1);
        assertFalse(phoneCall1.isSavedContact(), "Phone call should not be marked as saved contact");
    }

    @Test
    void GivenNumberInContacts_WhenAddPhoneCall_ThenPhoneCallAddedSavedContactIsTrue() {
        // Arrange
        when(contactRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.of(new Contact("Test", phoneCall1.getPhoneNumber())));
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());

        // Act
        phoneCallService.savePhoneCall(phoneCall1);

        // Assert
        verify(phoneCallRepository).save(phoneCall1);
        assertTrue(phoneCall1.isSavedContact(), "Phone call should be marked as saved contact");
    }

    @Test
    void GivenNumberFromBlacklist_WhenAddPhoneCall_ThenThrowBlockedNumberException() {
        // Arrange
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.of(new Blacklist(phoneCall1.getPhoneNumber())));

        // Act
        Exception exception = assertThrows(RuntimeException.class, () -> phoneCallService.savePhoneCall(phoneCall1));

        // Assert
        assertEquals("Received a call from a blocked number.", exception.getMessage());

        verify(phoneCallRepository, never()).save(phoneCall1);
    }


    //// ------- SEARCH BY DURATION -------------

    @Test
    void getPhoneCallsByNumber_WhenCallsExist_ThenReturnMatchingCalls() {
        String searchNumber = "0518617755";
        List<PhoneCall> expectedPhoneCalls = Arrays.asList(phoneCall1, phoneCall3); // Only phone calls with the search number

        when(phoneCallRepository.findByPhoneNumber(searchNumber)).thenReturn(expectedPhoneCalls);

        List<PhoneCall> result = phoneCallService.getPhoneCallsByPhoneNumber(searchNumber);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size(), "Should return exactly 2 phone calls for the specific number");
        assertTrue(result.stream().allMatch(pc -> pc.getPhoneNumber().equals(searchNumber)), "All returned phone calls should have the searched phone number");
    }
    @Test
    void getPhoneCallsByNumber_WhenNoCallsExist_ThenReturnEmptyList() {
        // Arrange
        String searchNumber = "0518617755";
        when(phoneCallRepository.findByPhoneNumber(searchNumber)).thenReturn(Collections.emptyList());

        // Act
        List<PhoneCall> result = phoneCallService.getPhoneCallsByPhoneNumber(searchNumber);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "No calls should be returned when none exist");
    }
    @Test
    void getPhoneCallsByDuration_WhenPositiveDuration_ThenReturnMatchingCalls() {
        // Arrange
        int searchDuration = 150;
        List<PhoneCall> expectedPhoneCalls = Collections.singletonList(phoneCall3);
        when(phoneCallRepository.findByDurationGreaterThan(searchDuration)).thenReturn(expectedPhoneCalls);

        // Act
        List<PhoneCall> result = phoneCallService.getPhoneCallsByDuration(searchDuration);

        // Assert
        assertNotNull(result);
        assertEquals(expectedPhoneCalls.size(), result.size(), "Returned calls should match the expected count");
        assertTrue(result.containsAll(expectedPhoneCalls), "Returned calls should match the expected calls");
    }

    @Test
    void getPhoneCallsByDuration_WhenNegativeDuration_ThenThrowException() {
        // Arrange
        int searchDuration = -50;

        // Act & Assert
        assertThrows(ValidationException.class, () -> phoneCallService.getPhoneCallsByDuration(searchDuration));
    }
    @Test
    void updatePhoneNumber_NotInContact_ToNewNumber_NotInContact() {
        // Arrange
        String oldPhoneNumber = "0518617756";
        String newPhoneNumber = "0591234567";
        PhoneCall existingCall = new PhoneCall("12-10-2021 13:40:50", "Incoming", "180", oldPhoneNumber, false);
        List<PhoneCall> existingCalls = Collections.singletonList(existingCall);

        when(phoneCallRepository.findByPhoneNumber(oldPhoneNumber)).thenReturn(existingCalls);
        when(contactRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.empty());
        when(blacklistRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.empty());

        // Act
        phoneCallService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber);

        // Assert
        verify(phoneCallRepository).save(any(PhoneCall.class));
        assertFalse(existingCall.isSavedContact(), "Updated call should not be marked as saved contact.");
    }
    @Test
    void updatePhoneNumber_NotInContactToContact_SavedContactTrue() {
        // Arrange
        String oldPhoneNumber = "0518617756";
        String newPhoneNumber = "0591234568";
        PhoneCall existingCall = new PhoneCall("13-10-2021 14:45:55", "Outgoing", "220", oldPhoneNumber, false);
        List<PhoneCall> existingCalls = Collections.singletonList(existingCall);

        when(phoneCallRepository.findByPhoneNumber(oldPhoneNumber)).thenReturn(existingCalls);
        when(contactRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.of(new Contact("John Doe", newPhoneNumber)));
        when(blacklistRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.empty());

        // Act
        phoneCallService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber);

        // Assert
        verify(phoneCallRepository).save(any(PhoneCall.class));
        assertTrue(existingCall.isSavedContact(), "Updated call should be marked as saved contact.");
    }
    @Test
    void updatePhoneNumber_ContactToNewNumber_SavedContactStaysTrue() {
        // Arrange
        String oldPhoneNumber = "0518617757";
        String newPhoneNumber = "0591234569";
        PhoneCall existingCall = new PhoneCall("14-10-2021 15:50:60", "Incoming", "300", oldPhoneNumber, true);
        List<PhoneCall> existingCalls = Collections.singletonList(existingCall);

        when(phoneCallRepository.findByPhoneNumber(oldPhoneNumber)).thenReturn(existingCalls);
        when(contactRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.of(new Contact("Jane Doe", newPhoneNumber)));
        when(blacklistRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.empty());

        // Act
        phoneCallService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber);

        // Assert
        verify(phoneCallRepository).save(any(PhoneCall.class));
        assertTrue(existingCall.isSavedContact(), "Updated call should stay marked as saved contact.");
    }
    @Test
    void updatePhoneNumber_NotInContactToBlacklistedNumber_ShouldThrowException() {
        // Arrange
        String oldPhoneNumber = "0518617758";
        String newPhoneNumber = "0591234570";
        when(phoneCallRepository.findByPhoneNumber(oldPhoneNumber)).thenReturn(Collections.emptyList());
        when(blacklistRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.of(new Blacklist(newPhoneNumber)));

        // Act & Assert
        assertThrows(BlockedPhoneNumberException.class, () -> phoneCallService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber));
    }
    @Test
    void updatePhoneNumber_ContactToBlacklistedNumber_ShouldThrowException() {
        // Arrange
        String oldPhoneNumber = "0518617759";
        String newPhoneNumber = "0591234571";
        when(phoneCallRepository.findByPhoneNumber(oldPhoneNumber)).thenReturn(Collections.emptyList());
        when(blacklistRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.of(new Blacklist(newPhoneNumber)));

        // Act & Assert
        assertThrows(BlockedPhoneNumberException.class, () -> phoneCallService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber));
    }
}
