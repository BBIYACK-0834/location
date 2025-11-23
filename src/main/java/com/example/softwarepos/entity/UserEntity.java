package com.example.softwarepos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userid;

    @Column(nullable = false)
    private String userpw;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String useraddress;

    @Column(nullable = false)
    private String userphone;

    @Column(unique = true, nullable = false)
    private String useremail;


    private String role = "USER";
}
