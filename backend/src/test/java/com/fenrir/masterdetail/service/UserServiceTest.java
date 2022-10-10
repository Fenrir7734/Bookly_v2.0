package com.fenrir.masterdetail.service;

import com.fenrir.masterdetail.dto.JwtTokenDTO;
import com.fenrir.masterdetail.dto.NewPasswordDTO;
import com.fenrir.masterdetail.dto.SignUpDTO;
import com.fenrir.masterdetail.dto.UserResponseDTO;
import com.fenrir.masterdetail.dto.mapper.UserMapper;
import com.fenrir.masterdetail.exception.DuplicateCredentialsException;
import com.fenrir.masterdetail.exception.PasswordMismatchException;
import com.fenrir.masterdetail.exception.ResourceNotFoundException;
import com.fenrir.masterdetail.model.Role;
import com.fenrir.masterdetail.model.User;
import com.fenrir.masterdetail.repository.UserRepository;
import com.fenrir.masterdetail.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    private static final String USERNAME = "User";
    private static final String EMAIL = "email@gmail.com";
    private static final String PLAIN_PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encoded";

    private User user;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    public void setUp() {
        this.user = User.builder()
                .id(1L)
                .username(USERNAME)
                .email(EMAIL)
                .firstname("John")
                .lastname("Smith")
                .role(Role.ROLE_USER)
                .password(ENCODED_PASSWORD)
                .build();

        this.userResponseDTO = new UserResponseDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getCreatedAt());
    }

    @Test
    public void registerUser_should_create_new_user() {
        final String email = "email@gmail.com";
        final String username = "User";

        SignUpDTO signUpDTO = new SignUpDTO("John", "Smith", username, email, PLAIN_PASSWORD);
        User mappedUser = User.builder()
                .firstname(signUpDTO.getFirstname())
                .lastname(signUpDTO.getLastname())
                .username(signUpDTO.getUsername())
                .email(signUpDTO.getEmail())
                .password(signUpDTO.getPassword())
                .role(Role.ROLE_USER)
                .build();

        User newUser = User.builder()
                .id(1L)
                .firstname(mappedUser.getFirstname())
                .lastname(mappedUser.getLastname())
                .username(mappedUser.getUsername())
                .email(mappedUser.getEmail())
                .password(ENCODED_PASSWORD)
                .role(mappedUser.getRole())
                .createdAt(LocalDateTime.of(2022, 10, 1, 1, 0, 0))
                .build();

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                newUser.getFirstname(),
                newUser.getLastname(),
                newUser.getUsername(),
                newUser.getCreatedAt());

        given(userRepository.existsByEmail(email))
                .willReturn(false);
        given(userRepository.existsByUsername(username))
                .willReturn(false);
        given(userMapper.fromUserRequestDTO(signUpDTO))
                .willReturn(mappedUser);
        given(passwordEncoder.encode(mappedUser.getPassword()))
                .willReturn(ENCODED_PASSWORD);
        given(userRepository.save(mappedUser))
                .willReturn(newUser);
        given(userMapper.toUserResponseDTO(newUser))
                .willReturn(userResponseDTO);

        UserResponseDTO responseDTO = userService.registerUser(signUpDTO);

        assertThat(responseDTO)
                .isNotNull();

        verify(userRepository, times(1)).existsByEmail(signUpDTO.getEmail());
        verify(userRepository, times(1)).existsByUsername(signUpDTO.getUsername());
        verify(userMapper, times(1)).fromUserRequestDTO(signUpDTO);
        verify(passwordEncoder, times(1)).encode(PLAIN_PASSWORD);
        verify(userRepository, times(1)).save(mappedUser);
        verify(userMapper, times(1)).toUserResponseDTO(newUser);
    }

    @Test
    public void registerUser_should_throw_exception_when_given_already_existing_email() {
        final String email = "email@gmail.com";

        SignUpDTO signUpDTO = new SignUpDTO("John", "Smith", "User", email, PLAIN_PASSWORD);

        given(userRepository.existsByEmail(email))
                .willReturn(true);

        assertThatThrownBy(() -> userService.registerUser(signUpDTO))
                .isInstanceOf(DuplicateCredentialsException.class)
                .hasMessage("Account with this email address already exists.");
    }

    @Test
    public void registerUser_should_throw_exception_when_given_already_existing_username() {
        final String username = "User";

        SignUpDTO signUpDTO = new SignUpDTO("John", "Smith", username, "email@gmail.com", PLAIN_PASSWORD);

        given(userRepository.existsByEmail(any()))
                .willReturn(false);
        given(userRepository.existsByUsername(username))
                .willReturn(true);

        assertThatThrownBy(() -> userService.registerUser(signUpDTO))
                .isInstanceOf(DuplicateCredentialsException.class)
                .hasMessage("Account with this username already exists.");
    }

    @Test
    public void updatePassword_should_update_password() {
        final String newPasswordEncoded = "New password encoded";
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO(PLAIN_PASSWORD, "pass", "pass");

        User userWithNewPassword = User.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .password(newPasswordEncoded)
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPasswordDTO.getOldPassword(), user.getPassword()))
                .willReturn(true);
        given(passwordEncoder.encode(newPasswordDTO.getNewPassword()))
                .willReturn(newPasswordEncoded);
        given(userRepository.save(user))
                .willReturn(userWithNewPassword);

        userService.updatePassword(USERNAME, newPasswordDTO);

        verify(passwordEncoder, times(1)).matches(newPasswordDTO.getOldPassword(), "encoded");
        verify(passwordEncoder, times(1)).encode(newPasswordDTO.getNewPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void updatePassword_should_throw_exception_when_given_wrong_old_password() {
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO("wrong password", "pass", "pass");

        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPasswordDTO.getOldPassword(), user.getPassword()))
                .willReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(USERNAME, newPasswordDTO))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessage("Provided password do not match current password");
    }

    @Test
    public void updatePassword_should_throw_exception_when_given_wrong_confirmation_password() {
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO(PLAIN_PASSWORD, "pass", "pass2");

        given(userRepository.findByUsername(any()))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any()))
                .willReturn(true);

        assertThatThrownBy(() -> userService.updatePassword(USERNAME, newPasswordDTO))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessage("New password and confirmation password are different");
    }

    @Test
    public void get_should_return_user_when_given_correct_username() {
        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(user));
        given(userMapper.toUserResponseDTO(user))
                .willReturn(userResponseDTO);

        UserResponseDTO actualUserResponseDto = userService.get(USERNAME);

        assertThat(actualUserResponseDto)
                .isEqualTo(userResponseDTO);

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userMapper, times(1)).toUserResponseDTO(user);
    }

    @Test
    public void get_should_throw_exception_when_given_wrong_username() {
        final String wrongUsername = "User";

        given(userRepository.findByUsername(wrongUsername))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.get(wrongUsername))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("User was not found for username=%s", wrongUsername));
        verify(userRepository, times(1)).findByUsername(wrongUsername);
    }

    @Test
    public void getAll_should_return_page_of_users() {
        Page<User> usersPage = new PageImpl<>(List.of(user));
        Page<UserResponseDTO> dtosPage = new PageImpl<>(List.of(userResponseDTO));
        Pageable pageable = PageRequest.of(1, 10);

        given(userRepository.findAll(pageable))
                .willReturn(usersPage);
        given(userMapper.toUserResponseDTO(user))
                .willReturn(userResponseDTO);

        Page<UserResponseDTO> actualPage = userService.getAll(pageable);

        assertThat(actualPage)
                .isEqualTo(dtosPage);
        verify(userRepository, times(1)).findAll(pageable);
        verify(userMapper, times(1)).toUserResponseDTO(user);
    }

    @Test
    public void updateRole_should_change_user_role() {
        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(user));
        given(userRepository.save(user))
                .willReturn(user);

        userService.updateRole(USERNAME, Role.ROLE_ADMIN);

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void updateRole_should_throw_exception_when_given_wrong_username() {
        String wrongUsername = "User";

        given(userRepository.findByUsername(wrongUsername))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateRole(wrongUsername, Role.ROLE_ADMIN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("User was not found for username=%s", wrongUsername));
        verify(userRepository, times(1)).findByUsername(wrongUsername);
    }

    @Test
    public void deleteByUsername_should_delete_user_when_given_correct_username() {
        given(userRepository.existsByUsername(USERNAME))
                .willReturn(true);
        willDoNothing().given(userRepository).deleteByUsername(USERNAME);

        userService.deleteByUsername(USERNAME);

        verify(userRepository, times(1)).existsByUsername(USERNAME);
        verify(userRepository, times(1)).deleteByUsername(USERNAME);
    }

    @Test
    public void deleteByUsername_should_throw_exception_when_given_wrong_username() {
        final String wrongUsername = "User";

        given(userRepository.existsByUsername(wrongUsername))
                .willReturn(false);

        assertThatThrownBy(() -> userService.deleteByUsername(USERNAME))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("User was not found for username=%s", wrongUsername));
    }

    @Test
    public void validateToken_should_return_true_given_correct_token() {
        JwtTokenDTO tokenDTO = new JwtTokenDTO("Token");

        given(jwtUtils.validateToken(tokenDTO.getAccessToken()))
                .willReturn(true);

        boolean result = userService.validateToken(tokenDTO);

        assertThat(result).isTrue();
    }

    @Test
    public void validateToken_should_return_false_given_wrong_token() {
        JwtTokenDTO tokenDTO = new JwtTokenDTO("Token");

        given(jwtUtils.validateToken(tokenDTO.getAccessToken()))
                .willReturn(false);

        boolean result = userService.validateToken(tokenDTO);

        assertThat(result).isFalse();
    }
}