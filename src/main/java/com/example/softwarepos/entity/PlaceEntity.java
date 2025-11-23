package com.example.softwarepos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "Place")
public class PlaceEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String Placename;

    private String PlaceExp;

    private String Category;

    private Long Likes;

    private String Comment;
    
    private String productImagePath;
    
    private Long Longitude;

    private Long Latitude;

}


    