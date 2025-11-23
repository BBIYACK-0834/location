package com.example.softwarepos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "orders")
public class ProductEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String proname; // 상품명

    private int proprice; // 상품 가격

    private String prosub; // 상품 세부 품목 ex) 상품명 : 갈색 후드티 세부 품목 : XL, L, M

    private String prointro; // 상품 설명
    
    private String productImagePath;
    

}
