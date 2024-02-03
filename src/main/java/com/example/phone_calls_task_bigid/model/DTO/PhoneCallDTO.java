package com.example.phone_calls_task_bigid.model.DTO;

public class PhoneCallDTO {
    private String time;
    private String callType;
    private int duration;
    private String phoneNumber;

    public PhoneCallDTO() {}

    public PhoneCallDTO(String time, String callType, String duration, String phoneNumber) {
        this.time = time;
        this.callType = callType;
        this.duration = Integer.parseInt(duration);
        this.phoneNumber = phoneNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

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
}
