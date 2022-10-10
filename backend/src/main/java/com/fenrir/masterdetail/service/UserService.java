package com.fenrir.masterdetail.service;

import com.fenrir.masterdetail.dto.*;
import com.fenrir.masterdetail.dto.mapper.UserMapper;
import com.fenrir.masterdetail.exception.DuplicateCredentialsException;
import com.fenrir.masterdetail.exception.PasswordMismatchException;
import com.fenrir.masterdetail.exception.ResourceNotFoundException;
import com.fenrir.masterdetail.model.Role;
import com.fenrir.masterdetail.model.User;
import com.fenrir.masterdetail.repository.UserRepository;
import com.fenrir.masterdetail.security.UserDetailsImpl;
import com.fenrir.masterdetail.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class UserService {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;

    @Transactional
    public UserResponseDTO registerUser(SignUpDTO signUpDTO) {
        if (userRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new DuplicateCredentialsException("Account with this email address already exists.");
        }
        if (userRepository.existsByUsername(signUpDTO.getUsername())) {
            throw new DuplicateCredentialsException("Account with this username already exists.");
        }

        User user = userMapper.fromUserRequestDTO(signUpDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    public JwtTokenDTO authenticateUser(SignInDTO signInDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDTO.getUsername(),
                        signInDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtils.generateJwtToken(principal);
        return userMapper.toJwtTokenDTO(token);
    }

    @Transactional
    public void updatePassword(String username, NewPasswordDTO passwordDTO) {
        User user = getByUsername(username);

        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new PasswordMismatchException("Provided password do not match current password");
        }

        String newPassword = passwordDTO.getNewPassword();
        if (!newPassword.equals(passwordDTO.getNewPasswordConfirmation())) {
            throw new PasswordMismatchException("New password and confirmation password are different");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    @Transactional
    public UserResponseDTO get(String username) {
        User user = getByUsername(username);
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    public Page<UserResponseDTO> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponseDTO);
    }

    @Transactional
    public UserResponseDTO create(SignUpDTO userRequestDTO) {
        User user = userMapper.fromUserRequestDTO(userRequestDTO);
        user = userRepository.save(user);
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    public void updateRole(String username, Role role) {
        User user = getByUsername(username);
        user.setRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void deleteByUsername(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new ResourceNotFoundException(String.format("User was not found for username=%s", username));
        }
        userRepository.deleteByUsername(username);
    }

    public boolean validateToken(JwtTokenDTO tokenDTO) {
        return jwtUtils.validateToken(tokenDTO.getAccessToken());
    }

    private User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User was not found for username=%s", username)
                ));
    }
}
