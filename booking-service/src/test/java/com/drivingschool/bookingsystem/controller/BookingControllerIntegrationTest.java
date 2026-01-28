package com.drivingschool.bookingsystem.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookingControllerIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(options().dynamicPort())
            .build();

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("user-service.base-url", () -> wireMock.getRuntimeInfo().getHttpBaseUrl());
    }

    private RestTestClient client;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setupStubs() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
        wireMock.stubFor(get("/api/users/1")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"role\":\"TRAINEE\"}")));
        wireMock.stubFor(get("/api/users/2")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":2,\"role\":\"INSTRUCTOR\"}")));
    }

    @Test
    void createBookingViaHttp() throws Exception {
        String payload = "{\"traineeId\":1,\"instructorId\":2," +
                "\"startTime\":\"2030-01-01T10:00:00\"," +
                "\"endTime\":\"2030-01-01T11:00:00\"}";

        client.post()
                .uri("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertThat(body).contains("\"status\":\"PENDING\"");
                });
    }
}
