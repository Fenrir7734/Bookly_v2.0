package com.fenrir.masterdetail.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String lastname;

    @NotBlank
    @Size(min = 4, max = 30)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 60, max = 60)
    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public User(String firstname, String lastname, String username, String email, String password, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
