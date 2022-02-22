package com.example.news.model;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class NewsProvider {
    @Id
    @Column(name = "code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;

    @Column(name = "name")
    private String name;
}
