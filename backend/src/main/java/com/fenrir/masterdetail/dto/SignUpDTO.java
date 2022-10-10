package com.fenrir.masterdetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Relation(itemRelation = "User", collectionRelation = "Users")
public class SignUpDTO {
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastname;

    @NotBlank
    @Size(min = 4, max = 30)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 30)
    private String password;
}
