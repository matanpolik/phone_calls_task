package com.example.phone_calls_task_bigid.model;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;


@Entity
@Table(name = "phoneCall_db")
public class PhoneCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @Column(name = "callType")
    private String callType; // incoming or outgoing call
    @Column(name = "duration")
    @Min(0)
    private int duration;
    @Column(name = "phoneNumber")
    @Pattern(regexp = "\\d+", message = "Phone number must contain only digits")
    private String phoneNumber;
    @Column(name = "savedContact")
    private boolean savedContact = false; // True if the number is saved contact number

    public PhoneCall() {
    }

    public PhoneCall(Date time, String callType, String duration, String phoneNumber, boolean savedContact) {
        this.time = time;
        this.callType = callType;
        this.duration = Integer.parseInt(duration);
        this.phoneNumber = phoneNumber;
        this.savedContact = savedContact;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Date getTime() {return time;}

    public void setTime(Date time) {this.time = time;}

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = Integer.parseInt(duration);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isSavedContact() {
        return savedContact;
    }

    public void setSavedContact(boolean savedContact) {
        this.savedContact = savedContact;
    }
}
