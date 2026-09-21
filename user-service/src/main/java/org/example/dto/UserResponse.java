package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Ответ с данными пользователя")
public class UserResponse extends RepresentationModel<UserResponse> {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Alice")
    private String name;

    @Schema(description = "Email пользователя", example = "alice@example.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "28")
    private Integer age;
}
