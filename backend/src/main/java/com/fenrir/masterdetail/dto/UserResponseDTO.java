package com.fenrir.masterdetail.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Relation(itemRelation = "User", collectionRelation = "Users")
public class UserResponseDTO {
    private String firstname;
    private String lastname;
    private String username;
    private LocalDateTime createdAt;
}
