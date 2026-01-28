package com.drivingschool.drivingschoolsystem.controller;

import com.drivingschool.drivingschoolsystem.model.User;
import com.drivingschool.drivingschoolsystem.model.User.Role;
import com.drivingschool.drivingschoolsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    private RestTestClient client;

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void resetDatabase() {

        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testCreateUser() {
        User newUser = new User(null, "TestName", "TestSurname", "test@example.com", null, Role.TRAINEE);

        client.post()
                .uri("/api/users")
                .body(newUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .consumeWith(result -> {
                    User created = result.getResponseBody();
                    assertThat(created).isNotNull();
                    assertThat(created.getId()).isNotNull();
                    assertThat(created.getEmail()).isEqualTo("test@example.com");
                });

        client.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User[].class)
                .consumeWith(result -> {
                    User[] users = result.getResponseBody();
                    assertThat(users).hasSize(1);
                });
    }
    @Test
    void testDeleteUser() {
        User userToDelete = userRepository.save(new User(null, "Delete", "Me", "delete@example.com", null, Role.TRAINEE));

        client.delete()
                .uri("/api/users/{id}", userToDelete.getId())
                .exchange()
                .expectStatus().isNoContent();

        client.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User[].class)
                .consumeWith(result -> {
                    User[] users = result.getResponseBody();
                    assertThat(users).hasSize(1);
                });
    }
}
