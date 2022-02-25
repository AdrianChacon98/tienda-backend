package com.store.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Setter
@Getter
@Table(name = "User")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class User implements UserDetails {


    @Transient
    private static final long serialVersionUID = 3859876590975261336L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    private String name;

    private String lastname;

    private String email;

    @JsonIgnore
    private String password;


    private Boolean locked;


    private Boolean enabled;

    @Column(name="created_at")
    private LocalDateTime createAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "verification_code")
    private String verificationCode;

    @JsonIgnore
    @OneToMany(mappedBy = "idUserAddress" , cascade = CascadeType.ALL)//fetch is lazy for defect
    private List<Address> addresses;


    @OneToMany(mappedBy = "idAuthorityUser", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Authorities> authorities;

    //this is not persistent because it doesnt save just set the value
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "id_role_user")
    private Role role;







    public User(){}


    public User(String name, String lastname, String email, String password, Boolean locked, Boolean enabled, LocalDateTime createAt, LocalDateTime expiredAt, String verificationCode, Role role) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.locked = locked;
        this.enabled = enabled;
        this.createAt = createAt;
        this.expiredAt = expiredAt;
        this.verificationCode = verificationCode;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = this.authorities
                .stream()
                .map(authority->new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());

        return authorities;
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    public String getEmail(){
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
