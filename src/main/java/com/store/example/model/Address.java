package com.store.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "address")
@Getter
@Setter
@ToString
public class Address implements Serializable {


    @Transient
    private static final long serialVersionUID = -244723336044305801L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="id_user_address")
    private User idUserAddress;


    private String street;

    private Integer number;

    private String appartment;

    private String country;

    private String state;

    private String county;

    @Column(name = "zip_code")
    private Integer zipCode;

    @Column(name="phone_number")
    private String phoneNumber;


    public Address(){}


    public Address(User idUserAddress, String street, Integer number, String appartment, String country, String state, String county, Integer zipCode, String phoneNumber) {
        this.idUserAddress = idUserAddress;
        this.street = street;
        this.number = number;
        this.appartment = appartment;
        this.country = country;
        this.state = state;
        this.county = county;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
    }



}
