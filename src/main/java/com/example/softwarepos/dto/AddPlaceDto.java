package com.example.softwarepos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddPlaceDto {
    private String placename;
    private String placeExp;
    private String category;
    private String longitude;
    private String latitude;
}
