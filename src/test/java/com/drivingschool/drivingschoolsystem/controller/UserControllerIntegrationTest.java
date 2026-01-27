package com.drivingschool.drivingschoolsystem.controller;

import com.drivingschool.drivingschoolsystem.model.User;
import com.drivingschool.drivingschoolsystem.model.User.Role;
import com.drivingschool.drivingschoolsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
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

    @Transactional
    @Test
    void testInitialUsersExist() {
        client.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User[].class)
                .consumeWith(result -> {
                    User[] users = result.getResponseBody();
                    assertThat(users).hasSize(2);
                    assertThat(users[0].getEmail()).isEqualTo("email1@example.com");
                    assertThat(users[1].getEmail()).isEqualTo("email2@example.com");
                });
    }

    @Transactional
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
                    assertThat(users).hasSize(3);
                });
    }
    @Transactional
    @Test
    void testDeleteUser() {
        User userToDelete = userRepository.findAll().get(0);

        client.delete()
                .uri("/api/users/{id}", userToDelete.getId())
                .exchange()
                .expectStatus().isOk();

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