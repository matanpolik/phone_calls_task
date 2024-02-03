package com.example.phone_calls_task_bigid.controller;

import com.example.phone_calls_task_bigid.model.DTO.PhoneCallDTO;
import com.example.phone_calls_task_bigid.model.PhoneCall;
import com.example.phone_calls_task_bigid.service.ContactService;
import com.example.phone_calls_task_bigid.service.PhoneCallService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping("/phone_calls")
@Validated
public class PhoneCallController {
    private final PhoneCallService phoneCallService;
    private final ContactService contactService;
    public PhoneCallController(PhoneCallService phoneCallService, ContactService contactService) {
        this.phoneCallService = phoneCallService;
        this.contactService = contactService;
    }

    /////-------- API #1 : ADD NEW PHONE CALL TO THE DB --------/////
    @PostMapping("/new-call")
    public void savePhoneCall(@RequestBody @Valid PhoneCallDTO phoneCallDTO){
        phoneCallService.savePhoneCall(phoneCallDTO);
    }

    /////-------- API #2 : GET ALL PHONE CALLS FROM SPECIFIC NUMBER --------/////
    @GetMapping("/search-by-number")
    public List<PhoneCall> getPhoneCallsByPhoneNumber(@RequestParam @Pattern(regexp = "\\d+", message = "Invalid phone number format") String phoneNumber) {
        return phoneCallService.getPhoneCallsByPhoneNumber(phoneNumber);
    }

    /////-------- API #3 : GET ALL THE PHONE CALLS WHERE DURATION BIGGER THAN [input] --------/////
    @GetMapping("/search-by-duration")
    public List<PhoneCall> getPhoneCallsByDuration(@RequestParam @Min(0) int duration) {
        return phoneCallService.getPhoneCallsByDuration(duration);
    }

    /////-------- API #4 : UPDATE PHONE NUMBER IN CONTACTS AND PHONE CALLS --------/////
    @PutMapping("/update-phone-number")
    public ResponseEntity<String> updatePhoneNumber(
            @RequestParam @NotBlank String oldPhoneNumber,
            @RequestParam @NotBlank String newPhoneNumber) {

        try {
            contactService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber);
            phoneCallService.updatePhoneNumber(oldPhoneNumber, newPhoneNumber);
            return ResponseEntity.ok("Phone number updated successfully in contacts and phone calls");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
