package org.example.expert.domain.auth.service;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원 가입 테스트 (성공)")
    void 회원_가입_성공_검증() {
        // given
        String email = "test@email.com";
        String password = "password";
        String role = "USER";

        SignupRequest signupRequest = new SignupRequest(email, password, role);
        given(userRepository.existsByEmail(any(String.class))).willReturn(false);

        UserRole userRole = UserRole.of(signupRequest.getUserRole());
        User user = new User(signupRequest.getEmail(),signupRequest.getPassword(),userRole);

        ReflectionTestUtils.setField(user, "id", 1L);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when

        SignupResponse response = authService.signup(signupRequest);

        // then

        Assertions.assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그인 테스트 (성공)")
    void 로그인_성공_검증() {
        // given
        String email = "test@email.com";
        String password = "password";

        SigninRequest signinRequest = new SigninRequest(email, password);
        User user = new User(signinRequest.getEmail(),signinRequest.getPassword(),UserRole.of("USER"));

        given(userRepository.findByEmail(any(String.class))).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(true);
        given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).willReturn("mockjwttoken");

        // when

        SigninResponse response = authService.signin(signinRequest);

        // then

        Assertions.assertThat(response.getBearerToken()).isEqualTo("mockjwttoken");
    }
}
