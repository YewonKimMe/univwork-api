package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PasswordDto {

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
}
