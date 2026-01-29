package com.drivingschool.drivingschoolsystem.controller;

import com.drivingschool.drivingschoolsystem.controller.dto.ErrorResponse;
import com.drivingschool.drivingschoolsystem.model.User;
import com.drivingschool.drivingschoolsystem.service.UserService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring injects a singleton service instance.")
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User list")
    })
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public User getUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
