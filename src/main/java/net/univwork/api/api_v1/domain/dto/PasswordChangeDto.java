package net.univwork.api.api_v1.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PasswordChangeDto {

    private String currentPwd;

    private String newPwd;

    private String newPwdCheck;

}
