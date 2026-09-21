package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание или обновление пользователя")
public class UserCreateRequest {

    @NotBlank(message = "Name is required")
    @Schema(description = "Имя пользователя", example = "Alice")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email пользователя", example = "alice@example.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "28")
    private Integer age;
}
