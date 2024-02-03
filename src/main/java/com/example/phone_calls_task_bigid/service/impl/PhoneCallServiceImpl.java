package com.example.phone_calls_task_bigid.service.impl;

import com.example.phone_calls_task_bigid.exception.BlockedPhoneNumberException;
import com.example.phone_calls_task_bigid.model.PhoneCall;
import com.example.phone_calls_task_bigid.repository.PhoneCallRepository;
import com.example.phone_calls_task_bigid.repository.BlacklistRepository;
import com.example.phone_calls_task_bigid.repository.ContactRepository;
import com.example.phone_calls_task_bigid.model.Blacklist;
import com.example.phone_calls_task_bigid.service.PhoneCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Service
public class PhoneCallServiceImpl implements PhoneCallService {

    private final PhoneCallRepository phoneCallRepository;
    private final BlacklistRepository blacklistRepository;
    private final ContactRepository contactRepository;

    @Autowired
    public PhoneCallServiceImpl(PhoneCallRepository phoneCallRepository,
                                BlacklistRepository blacklistRepository,
                                ContactRepository contactRepository) {
        this.phoneCallRepository = phoneCallRepository;
        this.blacklistRepository = blacklistRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public void savePhoneCall(@Valid PhoneCall phoneCall) {
        validateCall(phoneCall);
        enrichPhoneCallData(phoneCall);
        phoneCallRepository.save(phoneCall);
    }

    private void validateCall(@Valid PhoneCall phoneCall) {
        if (isPhoneNumberBlacklisted(phoneCall.getPhoneNumber())) {
            throw new BlockedPhoneNumberException();
        }
    }

    private void enrichPhoneCallData(PhoneCall phoneCall) {
        boolean isContactSaved = contactRepository.findByPhoneNumber(phoneCall.getPhoneNumber())
                .isPresent();
        phoneCall.setSavedContact(isContactSaved);
    }

    private boolean isPhoneNumberBlacklisted(String phoneNumber) {
        Optional<Blacklist> blacklistOptional = blacklistRepository.findByPhoneNumber(phoneNumber);
        return blacklistOptional.isPresent();
    }

    public List<PhoneCall> getPhoneCallsByPhoneNumber(String phoneNumber) {
        return phoneCallRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<PhoneCall> getPhoneCallsByDuration(int duration) {
        if (duration < 0) {
            throw new ValidationException("Duration must be greater than or equal to 0");
        }
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
