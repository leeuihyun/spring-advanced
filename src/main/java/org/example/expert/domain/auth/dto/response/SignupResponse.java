package org.example.expert.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class SignupResponse {

    private Long userId;

    @JsonCreator
    public SignupResponse(@JsonProperty("userId") Long userId) {
        this.userId = userId;
    }
}
