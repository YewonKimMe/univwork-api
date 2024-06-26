package net.univwork.api.api_v1.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignUpFormDto {

    @NotBlank(message = "아이디를 확인해 주세요.")
    private String id;

    @NotBlank(message = "비밀번호를 확인해 주세요.")
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#]{8,}$", message = "비밀번호는 최소 8자 이상으로, 영문자와 숫자를 포함해야 하며, 선택적으로 !, @, # 중 하나를 포함할 수 있습니다.")
    private String password;

    @Size(max = 8)
    @Pattern(regexp = "^(?!.*관리자)(?!.*운영자).*$", message = "사용 불가능한 닉네임 입니다.")
    private String username;

    @Email(message = "올바른 이메일을 입력해주세요.")
    private String email;

}
