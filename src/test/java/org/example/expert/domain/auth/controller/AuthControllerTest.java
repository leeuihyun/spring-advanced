package org.example.expert.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @MockBean
    AuthService authService;

    @MockBean
    JwtUtil jwtUtil;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입_컨트롤러_테스트() throws Exception {
        String email = "test@email.com";
        String password = "password";
        String userRole = "USER";

        SignupRequest request = new SignupRequest(email, password, userRole);
        SignupResponse response = new SignupResponse(1L);

        given(authService.signup(any(SignupRequest.class))).willReturn(response);


        // when
        ResultActions result = mockMvc.perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        SignupResponse actualResult = objectMapper.readValue(contentAsString, SignupResponse.class);

        result.andExpect(status().isOk());
        Assertions.assertThat(actualResult.getUserId()).isEqualTo(1L);
    }
}
