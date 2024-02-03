package com.example.phone_calls_task_bigid.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.phone_calls_task_bigid.exception.BlockedPhoneNumberException;
import com.example.phone_calls_task_bigid.model.BlockedNumber;
import com.example.phone_calls_task_bigid.model.Contact;
import com.example.phone_calls_task_bigid.model.DTO.PhoneCallDTO;
import com.example.phone_calls_task_bigid.model.PhoneCall;
import com.example.phone_calls_task_bigid.repository.BlockedNumberRepository;
import com.example.phone_calls_task_bigid.repository.ContactRepository;
import com.example.phone_calls_task_bigid.repository.PhoneCallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
public class PhoneCallServiceImplTest {
    @Mock
    private PhoneCallRepository phoneCallRepository;
    @Mock
    private BlockedNumberRepository blacklistRepository;
    @Mock
    private ContactRepository contactRepository;
    @InjectMocks
    private PhoneCallServiceImpl phoneCallService;
    private PhoneCall phoneCall1;
    private PhoneCall phoneCall3;
    private PhoneCallDTO phoneCallDTO1;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Date date1 = dateFormat.parse("08-10-2021 09:10:30");
    Date date3 = dateFormat.parse("10-10-2021 11:20:30");

    public PhoneCallServiceImplTest() throws ParseException {
    }

    @BeforeEach
    void setUp(){
        phoneCall1 = new PhoneCall(date1, "Outgoing", "179", "0518617755", true);
        phoneCall3 = new PhoneCall(date3, "Outgoing", "200", "0518617755", true);

        phoneCallDTO1 = new PhoneCallDTO("08-10-2021 09:10:30", "Outgoing", "179", "0518617755");
    }

    @Test
    void GivenNumberNotInContacts_WhenAddPhoneCall_ThenPhoneCallAdded() {
        // Arrange
        when(contactRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        ArgumentCaptor<PhoneCall> phoneCallArgumentCaptor = ArgumentCaptor.forClass(PhoneCall.class);

        // Act
        phoneCallService.savePhoneCall(phoneCallDTO1);

        // Assert
        verify(phoneCallRepository).save(phoneCallArgumentCaptor.capture());
        PhoneCall capturedPhoneCall = phoneCallArgumentCaptor.getValue();

        // Now assert the properties of the captured PhoneCall
        assertAll("Should capture PhoneCall with correct properties",
                () -> assertEquals(date1, capturedPhoneCall.getTime()),
                () -> assertEquals("Outgoing", capturedPhoneCall.getCallType()),
                () -> assertEquals(179, capturedPhoneCall.getDuration()),
                () -> assertEquals("0518617755", capturedPhoneCall.getPhoneNumber()),
                () -> assertFalse(capturedPhoneCall.isSavedContact())
        );
    }

    @Test
    void GivenNumberInContacts_WhenAddPhoneCall_ThenPhoneCallAddedSavedContactIsTrue() {
        // Arrange
        when(contactRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.of(new Contact("Test", phoneCall1.getPhoneNumber())));
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        ArgumentCaptor<PhoneCall> phoneCallArgumentCaptor = ArgumentCaptor.forClass(PhoneCall.class);

        // Act
        phoneCallService.savePhoneCall(phoneCallDTO1);

        // Assert
        verify(phoneCallRepository).save(phoneCallArgumentCaptor.capture());
        PhoneCall capturedPhoneCall = phoneCallArgumentCaptor.getValue();

        // Now assert the properties of the captured PhoneCall
        assertAll("Should capture PhoneCall with correct properties",
                () -> assertEquals(date1, capturedPhoneCall.getTime()),
                () -> assertEquals("Outgoing", capturedPhoneCall.getCallType()),
                () -> assertEquals(179, capturedPhoneCall.getDuration()),
                () -> assertEquals("0518617755", capturedPhoneCall.getPhoneNumber()),
                () -> assertTrue(capturedPhoneCall.isSavedContact())
        );
    }

    @Test
    void GivenNumberFromBlacklist_WhenAddPhoneCall_ThenThrowBlockedNumberException() {
        // Arrange
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.of(new BlockedNumber(phoneCall1.getPhoneNumber())));

        // Act & Assert
        assertThrows(BlockedPhoneNumberException.class, () -> phoneCallService.savePhoneCall(phoneCallDTO1));
        verify(phoneCallRepository, never()).save(any(PhoneCall.class));
    }
    @Test
    void GivenWrongCallType_WhenAddPhoneCall_ThenThrowException() {
        // Arrange
        when(contactRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        phoneCallDTO1.setCallType("NotIncomingOrOutgoing");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> phoneCallService.savePhoneCall(phoneCallDTO1));
        verify(phoneCallRepository, never()).save(any(PhoneCall.class));
    }
    @Test
    void GivenWrongDuration_WhenAddPhoneCall_ThenPhoneCallNotAddedToDB() {
        // Arrange
        when(contactRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        phoneCallDTO1.setDuration("-5");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> phoneCallService.savePhoneCall(phoneCallDTO1));
        verify(phoneCallRepository, never()).save(any(PhoneCall.class));
    }
    @Test
    void GivenWrongDate_WhenAddPhoneCall_ThenThrowException() {
        // Arrange
        when(contactRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        phoneCallDTO1.setTime("10-10-10 09:10:30");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> phoneCallService.savePhoneCall(phoneCallDTO1));
        verify(phoneCallRepository, never()).save(any(PhoneCall.class));
    }
    @Test
    void GivenWrongPhoneNumber_WhenAddPhoneCall_ThenThrowException() {
        // Arrange
        when(contactRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        when(blacklistRepository.findByPhoneNumber(phoneCall1.getPhoneNumber())).thenReturn(Optional.empty());
        phoneCallDTO1.setPhoneNumber("NOT_PHONE_NUMBER");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> phoneCallService.savePhoneCall(phoneCallDTO1));
        verify(phoneCallRepository, never()).save(any(PhoneCall.class));
    }

    //// ------- SEARCH BY DURATION -------------

    @Test
    void getPhoneCallsByNumber_WhenCallsExist_ThenReturnMatchingCalls() {
        String searchNumber = "0518617755";
        List<PhoneCall> expectedPhoneCalls = Arrays.asList(phoneCall1, phoneCall3); // Only phone calls with the search number

        when(phoneCallRepository.findByPhoneNumber(searchNumber)).thenReturn(expectedPhoneCalls);

        List<PhoneCall> result = phoneCallService.getPhoneCallsByPhoneNumber(searchNumber);

        // Assertions
        //assertNotNull(result);
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
    void updatePhoneNumber_NotInContact_ToNewNumber_NotInContact() {
        // Arrange
        String oldPhoneNumber = "0518617756";
        String newPhoneNumber = "0591234567";
        PhoneCall existingCall = new PhoneCall(date1, "Incoming", "180", oldPhoneNumber, false);
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
        PhoneCall existingCall = new PhoneCall(date1, "Outgoing", "220", oldPhoneNumber, false);
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
        PhoneCall existingCall = new PhoneCall(date1, "Incoming", "300", oldPhoneNumber, true);
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
        when(blacklistRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.of(new BlockedNumber(newPhoneNumber)));

        // Act & Assert
        assertThrows(BlockedPhoneNumberException.class, () -> phoneCallService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber));
    }
    @Test
    void updatePhoneNumber_ContactToBlacklistedNumber_ShouldThrowException() {
        // Arrange
        String oldPhoneNumber = "0518617759";
        String newPhoneNumber = "0591234571";
        when(phoneCallRepository.findByPhoneNumber(oldPhoneNumber)).thenReturn(Collections.emptyList());
        when(blacklistRepository.findByPhoneNumber(newPhoneNumber)).thenReturn(Optional.of(new BlockedNumber(newPhoneNumber)));

        // Act & Assert
        assertThrows(BlockedPhoneNumberException.class, () -> phoneCallService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber));
    }
}
