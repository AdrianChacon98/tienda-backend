package com.store.example.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Authorities implements Serializable {

    @Transient
    private static final long serialVersionUID = 9035392881549994705L;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name="id_user_authoritie")
    private User idAuthorityUser;



    @Column(name="name")
    private String name;

    public Authorities(){}

    public Authorities(User idAuthorityUser, String name) {
        this.idAuthorityUser = idAuthorityUser;
        this.name = name;
    }
}
