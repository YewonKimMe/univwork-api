package net.univwork.api.api_v1.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * SuccessResultAndMessage<br>
 * String HttpCode: HttpStatus.__Code__,<br>
 * String message
 * */
@Data
@RequiredArgsConstructor
public class SuccessResultAndMessage {

    private final String HttpCode;

    private final String message;
}
