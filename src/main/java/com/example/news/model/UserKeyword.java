package com.example.news.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserKeyword implements Serializable {
    @Id
    @Column(name = "code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;

    @ManyToOne
    @JoinColumn(name = "userCode", referencedColumnName = "code")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "keywordCode", referencedColumnName = "code")
    private Keyword keyword;

    @ManyToOne
    @JoinColumn(name = "providerCode", referencedColumnName = "code")
    private NewsProvider newsProvider;
}
