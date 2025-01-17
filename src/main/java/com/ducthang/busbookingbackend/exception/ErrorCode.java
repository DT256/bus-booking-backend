package com.ducthang.busbookingbackend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum ErrorCode
{
    // Auth and validation
    UNAUTHENTICATED(1100, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1101, "You do not have permission to do this", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1102, "Invalid Token", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1103, "Invalid request", HttpStatus.BAD_REQUEST),
    EMAIL_EXIST_REGISTER(1104, "This email has been used", HttpStatus.CONFLICT),
    USERNAME_EXIST_REGISTER(1105, "This username has been used", HttpStatus.CONFLICT),

    // User
    USER_NOT_EXIST(1200, "The user does not exist", HttpStatus.NOT_FOUND),

    ;
    private final int responseCode;
    private final String message;
    private final HttpStatusCode httpStatusCode;
}
