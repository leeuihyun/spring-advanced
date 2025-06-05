package org.example.expert.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordRequest {

    @NotBlank
    private String oldPassword;

    @Size(min = 8, message = "8자 이상만 작성 가능합니다.")
    @Pattern(regexp = "(?=.*[A-Z])(?=.*\\d).+", message = "대문자, 숫자를 하나 이상 포함해야합니다.")
    private String newPassword;
}
