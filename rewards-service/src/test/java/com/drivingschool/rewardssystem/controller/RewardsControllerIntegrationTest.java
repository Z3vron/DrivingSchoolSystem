package com.drivingschool.rewardssystem.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RewardsControllerIntegrationTest {

    private RestTestClient client;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUpClient() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void earnPointsViaHttp() throws Exception {
        String payload = "{\"traineeId\":5,\"points\":20}";

        client.post()
                .uri("/api/rewards/earn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertThat(body).contains("\"points\":20");
                });
    }

    @Test
    void earnPointsValidationError() {
        String payload = "{\"traineeId\":5,\"points\":0}";

        client.post()
                .uri("/api/rewards/earn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertThat(body).contains("points");
                });
    }
}
