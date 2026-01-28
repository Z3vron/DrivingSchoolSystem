package com.drivingschool.bookingsystem.client;

import com.drivingschool.bookingsystem.exception.BusinessRuleException;
import com.drivingschool.bookingsystem.exception.ExternalServiceException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring injects a shared RestTemplate bean.")
    public UserClient(RestTemplate restTemplate,
                      @Value("${user-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public UserSummary getUser(Long id) {
        try {
            ResponseEntity<UserSummary> response = restTemplate.getForEntity(
                    baseUrl + "/api/users/" + id, UserSummary.class);
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new BusinessRuleException("User not found in user-service");
            }
            throw new ExternalServiceException("User service error: " + ex.getStatusCode());
        }
    }
}
