package com.example.phone_calls_task_bigid.service.impl;

import com.example.phone_calls_task_bigid.exception.BlockedPhoneNumberException;
import com.example.phone_calls_task_bigid.model.DTO.PhoneCallDTO;
import com.example.phone_calls_task_bigid.model.PhoneCall;
import com.example.phone_calls_task_bigid.repository.PhoneCallRepository;
import com.example.phone_calls_task_bigid.repository.BlockedNumberRepository;
import com.example.phone_calls_task_bigid.repository.ContactRepository;
import com.example.phone_calls_task_bigid.service.PhoneCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class PhoneCallServiceImpl implements PhoneCallService {

    private final PhoneCallRepository phoneCallRepository;
    private final BlockedNumberRepository blacklistRepository;
    private final ContactRepository contactRepository;

    @Autowired
    public PhoneCallServiceImpl(PhoneCallRepository phoneCallRepository,
                                BlockedNumberRepository blacklistRepository,
                                ContactRepository contactRepository) {
        this.phoneCallRepository = phoneCallRepository;
        this.blacklistRepository = blacklistRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public void savePhoneCall(PhoneCallDTO phoneCallDTO) {
        validateCall(phoneCallDTO);
        PhoneCall phoneCall = convertToPhoneCall(phoneCallDTO);
        validateAndSavePhoneCall(phoneCall);
    }

    private PhoneCall convertToPhoneCall(PhoneCallDTO phoneCallDTO) {
        PhoneCall phoneCall = new PhoneCall();
        // Convert String time to Date
        Date parsedDate = parseStringToDate(phoneCallDTO.getTime());
        phoneCall.setTime(parsedDate);
        phoneCall.setCallType(phoneCallDTO.getCallType());
        phoneCall.setDuration(String.valueOf(phoneCallDTO.getDuration()));
        phoneCall.setPhoneNumber(phoneCallDTO.getPhoneNumber());
        // Initially set savedContact to false; it will be updated in enrichPhoneCallData if necessary
        phoneCall.setSavedContact(false);
        return phoneCall;
    }

    private Date parseStringToDate(String timeString) {
        // Use regular expression to match the required date format
        String dateFormatPattern = "\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}";
        if (!timeString.matches(dateFormatPattern)) {
            throw new IllegalArgumentException("Invalid date format");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            return dateFormat.parse(timeString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing date", e);
        }
    }
    private void validateAndSavePhoneCall(PhoneCall phoneCall) {
        enrichPhoneCallData(phoneCall);
        phoneCallRepository.save(phoneCall);
    }
    private void validateCall(PhoneCallDTO phoneCallDTO) {
        //Blacklist validation
        if (isPhoneNumberBlacklisted(phoneCallDTO.getPhoneNumber())) {
            throw new BlockedPhoneNumberException();
        }
        if (!Objects.equals(phoneCallDTO.getCallType(), "Incoming") && !Objects.equals(phoneCallDTO.getCallType(), "Outgoing")) {
            throw new IllegalArgumentException("Invalid Phone Call Type!");
        }
        if (phoneCallDTO.getDuration() < 0){
            throw new IllegalArgumentException("Invalid Duration");
        }
        validatePhoneNumber(phoneCallDTO.getPhoneNumber());
    }
    public void validatePhoneNumber(String phoneNumber) {
        final String PHONE_NUMBER_REGEX = "^\\+?0*[0-9]\\d{1,20}$";
        if (phoneNumber == null || !phoneNumber.matches(PHONE_NUMBER_REGEX)) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }
        // Add additional checks here if necessary
    }
    private void enrichPhoneCallData(PhoneCall phoneCall) {
        boolean isContactSaved = contactRepository.findByPhoneNumber(phoneCall.getPhoneNumber()).isPresent();
        phoneCall.setSavedContact(isContactSaved);
    }

    private boolean isPhoneNumberBlacklisted(String phoneNumber) {
        return blacklistRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public List<PhoneCall> getPhoneCallsByPhoneNumber(String phoneNumber) {
        return phoneCallRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<PhoneCall> getPhoneCallsByDuration(int duration) {
        return phoneCallRepository.findByDurationGreaterThan(duration);
    }

    @Override
    @Transactional
    public void updatePhoneNumber(String oldPhoneNumber, String newPhoneNumber) {
        // check if number is in the black list
        if (isPhoneNumberBlacklisted(newPhoneNumber)) {
            throw new BlockedPhoneNumberException();
        }
        // Perform the update in the phoneCallRepository
        List<PhoneCall> phoneCalls = phoneCallRepository.findByPhoneNumber(oldPhoneNumber);
        // Check if the new phone number is in the list of contacts
        boolean isNewNumberInContacts = contactRepository.findByPhoneNumber(newPhoneNumber).isPresent();
        phoneCalls.forEach(phoneCall -> {
            phoneCall.setPhoneNumber(newPhoneNumber);
            phoneCall.setSavedContact(isNewNumberInContacts);
            phoneCallRepository.save(phoneCall);
        });
    }
}
