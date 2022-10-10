package com.fenrir.masterdetail.assembler;

import com.fenrir.masterdetail.controller.UserController;
import com.fenrir.masterdetail.dto.UserResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponseDTO, EntityModel<UserResponseDTO>> {
    @Override
    public EntityModel<UserResponseDTO> toModel(UserResponseDTO user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).getUserByUsername(user.getUsername())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers(null)).withRel("Users")
        );
    }
}
