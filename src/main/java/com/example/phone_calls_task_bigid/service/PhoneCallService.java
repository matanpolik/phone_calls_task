package com.example.phone_calls_task_bigid.service;

import com.example.phone_calls_task_bigid.model.DTO.PhoneCallDTO;
import com.example.phone_calls_task_bigid.model.PhoneCall;
import java.util.List;
/**
 * Service interface for managing phone calls.
 */
public interface PhoneCallService {
    /**
     * Saves a phone call to the database.
     *
     * @param phonecall The phone call to be saved.
     */
    void savePhoneCall(PhoneCallDTO phonecallDTO);

    /**
     * Retrieves all phone calls associated with a specific phone number.
     *
     * @param phoneNumber The phone number to search for.
     * @return A list of phone calls with the specified phone number.
     */
    List<PhoneCall> getPhoneCallsByPhoneNumber(String phoneNumber);

    /**
     * Retrieves phone calls with a duration greater than the specified value.
     *
     * @param duration The minimum duration for phone calls to be included.
     * @return A list of phone calls with a duration greater than the specified value.
     */
    List<PhoneCall> getPhoneCallsByDuration(int duration);

    /**
     * Updates the phone number of existing phone calls from an old number to a new number.
     *
     * @param oldPhoneNumber The current phone number to be updated.
     * @param newPhoneNumber The new phone number to replace the old one.
     */
    void updatePhoneNumber(String oldPhoneNumber, String newPhoneNumber);
}
