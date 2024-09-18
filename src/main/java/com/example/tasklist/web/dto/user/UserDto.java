package com.example.tasklist.web.dto.user;

import com.example.tasklist.web.dto.validation.OnCreate;
import com.example.tasklist.web.dto.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "User DTO")
public class UserDto {

    @Schema(description = "User ID", example = "1")
    @NotNull(message = "Id must be not NULL.", groups = OnUpdate.class)
    private Long id;

    @Schema(description = "User name", example = "Mila Samilyak")
    @NotNull(message = "Name must be not NULL.",
            groups = {OnUpdate.class, OnCreate.class})
    @Length(max = 255,
            message = "Name length must be smaller then 255 symbols.",
            groups = {OnUpdate.class, OnCreate.class}
    )
    private String name;

    @Schema(description = "Email", example = "mila.samilyak@gmail.com")
    @NotNull(message = "Username must be not NULL.",
            groups = {OnUpdate.class, OnCreate.class})
    @Length(max = 255,
            message = "Username length must be smaller then 255 symbols.",
            groups = {OnUpdate.class, OnCreate.class}
    )
    private String email;

    @Schema(description = "User password", example = "12345")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "The password must be not NULL.",
            groups = {OnUpdate.class, OnCreate.class})
    private String password;

    @Schema(description = "User password confirmation", example = "12345")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "The passwordConfirmation must be not NULL.",
            groups = {OnCreate.class})
    private String passwordConfirmation;

}
