package com.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    @Column(name = "phone") 
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip")
    private String zip;

    @Column(name = "country")
    private String country;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}
