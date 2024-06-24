package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignUpIdDto {

    @NotBlank(message = "아이디를 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$", message = "아이디는 최소 6자 이상으로, 영문자와 숫자를 포함해야 합니다.")
    private String id;
}
