package com.fenrir.masterdetail.controller;

import com.fenrir.masterdetail.assembler.UserModelAssembler;
import com.fenrir.masterdetail.dto.NewPasswordDTO;
import com.fenrir.masterdetail.dto.SignUpDTO;
import com.fenrir.masterdetail.dto.UserResponseDTO;
import com.fenrir.masterdetail.model.Role;
import com.fenrir.masterdetail.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping(
        path = "/api/users",
        produces = "application/hal+json"
)
public class UserController {
    private UserService userService;
    private UserModelAssembler userAssembler;
    private PagedResourcesAssembler<UserResponseDTO> pagedAssembler;

    @GetMapping(path = "/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable("username") String username) {
        UserResponseDTO user = userService.get(username);
        return ResponseEntity.ok(userAssembler.toModel(user));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @PageableDefault(sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserResponseDTO> users = userService.getAll(pageable);
        return ResponseEntity.ok(pagedAssembler.toModel(users, userAssembler));
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{username}/grant/{role}")
    public ResponseEntity<?> grantRole(@PathVariable("username") String username, @PathVariable("role") Role role) {
        userService.updateRole(username, role);
        return ResponseEntity.noContent().build();
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(path = "/{username}")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable("username") String username) {
        userService.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(path = "/{username}/change-pass")
    public ResponseEntity<?> changePassword(
            @PathVariable("username") String username,
            @RequestBody NewPasswordDTO passwordDTO) {

        userService.updatePassword(username, passwordDTO);
        return ResponseEntity.noContent().build();
    }
}
