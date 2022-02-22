package com.example.news.model;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Keyword {
    @Id
    @Column(name = "code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;

    @Column(name = "name", unique = true)
    private String name;
}
