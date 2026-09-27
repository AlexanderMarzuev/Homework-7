package org.example;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserCreateRequest;
import org.example.dto.UserResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Users", description = "CRUD-операции над пользователями")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Создать пользователя",
            description = "Создаёт нового пользователя и возвращает его с ссылками HATEOAS")
    @ApiResponse(responseCode = "201", description = "Пользователь создан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> create(
            @RequestBody @Valid UserCreateRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        User saved = userService.create(user);
        UserResponse response = toResponse(saved);
        EntityModel<UserResponse> model = EntityModel.of(response,
                linkTo(methodOn(UserController.class).getById(saved.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"),
                linkTo(methodOn(UserController.class).delete(saved.getId())).withRel("delete"));
        return ResponseEntity
                .created(URI.create("/api/users/" + saved.getId()))
                .body(model);
    }

    @Operation(summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> getById(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        User user = userService.findById(id);
        UserResponse response = toResponse(user);
        EntityModel<UserResponse> model = EntityModel.of(response,
                linkTo(methodOn(UserController.class).getById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"),
                linkTo(methodOn(UserController.class).update(id, null)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(id)).withRel("delete"));
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей с ссылками HATEOAS")
    @ApiResponse(responseCode = "200", description = "Список получен")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponse>>> getAll() {
        List<EntityModel<UserResponse>> users = userService.findAll().stream()
                .map(u -> EntityModel.of(toResponse(u),
                        linkTo(methodOn(UserController.class).getById(u.getId())).withSelfRel()))
                .toList();
        CollectionModel<EntityModel<UserResponse>> collection = CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel());
        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Обновить пользователя",
            description = "Обновляет данные пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> update(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid UserCreateRequest request) {
        User user = new User();
        user.setId(id);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        User saved = userService.update(user);
        UserResponse response = toResponse(saved);
        EntityModel<UserResponse> model = EntityModel.of(response,
                linkTo(methodOn(UserController.class).getById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"));
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Удалить пользователя",
            description = "Удаляет пользователя по ID")
    @ApiResponse(responseCode = "204", description = "Пользователь удалён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAge());
    }
}
