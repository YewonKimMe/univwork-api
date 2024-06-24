package net.univwork.api.api_v1.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * SuccessResultAndMessage<br>
 * String HttpCode: HttpStatus.__Code__,<br>
 * String message
 * */
@Data
@AllArgsConstructor
public class SuccessResultAndMessage {

    private String HttpCode;

    private String message;
}
