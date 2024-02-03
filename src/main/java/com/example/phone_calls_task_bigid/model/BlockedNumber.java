package com.example.phone_calls_task_bigid.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "blacklist")
public class BlockedNumber {

    @Id
    @Column(name = "phoneNumber")
    private String phoneNumber;

    public BlockedNumber() {}

    public BlockedNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}