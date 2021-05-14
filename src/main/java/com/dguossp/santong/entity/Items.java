package com.dguossp.santong.entity;

import javax.persistence.*;

@Entity
@Table(name = "items")
public class Items {

    // 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 아이템 명
    private String name;
}
