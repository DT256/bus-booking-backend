package com.group8.busbookingbackend.dto.auth.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse
{
    private String jwt;
    private String message;
}
