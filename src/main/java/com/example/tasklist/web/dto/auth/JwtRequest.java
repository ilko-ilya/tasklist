package com.example.tasklist.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request for Login")
public class JwtRequest {

    @Schema(description = "Email", example = "mila.samilyak@gmail.com")
    @NotNull(message = "Username must be not NULL.")
    @Email(message = "Invalid email format.")
    private String email;

    @Schema(description = "Password", example = "12345")
    @NotNull(message = "Password must be not NULL.")
    private String password;

}
