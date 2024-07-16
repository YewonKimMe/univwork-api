package net.univwork.api.api_v1.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ErrorResultAndMessage<br>
 * String HttpCode: HttpStatus.__Code__,<br>
 * String message
 * */
@Data
@AllArgsConstructor
public class ErrorResultAndMessage implements ResultAndMessage{

    private String HttpCode;

    private Object message;
}
