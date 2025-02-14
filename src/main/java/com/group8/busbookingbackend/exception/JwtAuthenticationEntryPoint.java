package com.group8.busbookingbackend.exception;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint
{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException
    {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getResponseCode());
        apiResponse.setMessage(errorCode.getMessage());
        response.setStatus(errorCode.getHttpStatusCode().value());
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter printWriter = response.getWriter();
        printWriter.write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
