package com.example.quizizz.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "type_account", nullable = false)
    private String typeAccount;

    @Column(name = "birthday", nullable = false)
    private LocalDate dob;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Column(name = "is_online", nullable = false)
    private boolean isOnline;

    @Column(name = "last_online_time")
    private LocalDate lastOnlineTime;
}
