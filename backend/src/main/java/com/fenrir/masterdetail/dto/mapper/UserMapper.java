package com.fenrir.masterdetail.dto.mapper;

import com.fenrir.masterdetail.dto.JwtTokenDTO;
import com.fenrir.masterdetail.dto.SignUpDTO;
import com.fenrir.masterdetail.dto.UserResponseDTO;
import com.fenrir.masterdetail.model.Role;
import com.fenrir.masterdetail.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDTO toUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getCreatedAt()
        );
    }

    public User fromUserRequestDTO(SignUpDTO user) {
        return new User(
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                Role.ROLE_USER
        );
    }

    public JwtTokenDTO toJwtTokenDTO(String token) {
        return new JwtTokenDTO(token);
    }

}
