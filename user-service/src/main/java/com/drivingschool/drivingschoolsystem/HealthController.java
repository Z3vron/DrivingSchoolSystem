package com.drivingschool.drivingschoolsystem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class HealthController {

    @GetMapping("/health")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service healthy")
    })
    public String health() {
        return "OK";
    }
}
