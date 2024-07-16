package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordFindDto {

    @NotBlank(message = "인증 토큰이 없습니다.")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "올바르지 않은 인증 토큰입니다.")
    private String authToken;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
}
