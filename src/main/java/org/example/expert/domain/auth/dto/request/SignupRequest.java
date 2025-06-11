package org.example.expert.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String userRole;

    public SignupRequest(String email, String password, String userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
}
