package com.example.phone_calls_task_bigid.model;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "contacts")
public class Contact implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "name", nullable = false)
    private String name;

    // Constructors
    public Contact() {
    }

    public Contact(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
