package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignUpEmailDto {

    @NotBlank
    @Email(message = "올바른 이메일을 입력해주세요.")
    private String email;
}
