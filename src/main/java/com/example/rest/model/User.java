package com.example.rest.model;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Column
    private String username;
    @Column
    private String nickname;
    @Column
    private String password;
//    @Column
//    private Date registrationdate;
//    @Column
//    private Date updatedate;
    @Column(name = "pic_url")
    private String picUrl;
    @Column
    private boolean verify;
    @Column
    private String token;

}
