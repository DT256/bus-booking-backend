package com.ducthang.busbookingbackend.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserLoginRequest
{
    @NotBlank(message = "You must enter the email")
    @Length(min = 6, max = 40, message = "Email's length must between 6 and 40")
    @Email(message = "This must be an email")
    private String email;
    @NotBlank(message = "You must enter the password")
    @Length(min = 6, max = 40, message = "Password's length must between 6 and 40")
    private String password;
}
