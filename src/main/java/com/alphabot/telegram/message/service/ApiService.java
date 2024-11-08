package com.alphabot.telegram.message.service;

import com.alphabot.telegram.message.model.*;
import jakarta.ws.rs.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

@Service
public class ApiService {

    Logger logger = LoggerFactory.getLogger(ApiService.class);

    private final RestTemplate restTemplate;

    @Value("${telegram.bot.base.url}")
    private String BASE_URL;

    public ApiService() {
        this.restTemplate = new RestTemplate();
    }

    // Test connection with GET
    public boolean testConnection() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    BASE_URL + "/test",  // endpoint for health check
                    String.class
            );

            logger.info("Connection test status: {}", response.getStatusCode());
            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            logger.error("Connection test failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean getTelegramClientData(Long telegramId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    BASE_URL + "/client/" + telegramId,  // endpoint for health check
                    String.class
            );

            logger.info("Connection test status: {}", response.getStatusCode());
            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            logger.error("Connection test failed: {}", e.getMessage());
            return false;
        }
    }

    public RegisterForRafflesResponse registerForRaffles(RegisterForRaffles registerForRaffles) {
        try {
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request entity
            HttpEntity<RegisterForRaffles> requestEntity =
                    new HttpEntity<>(registerForRaffles, headers);

            // Make POST request
            ResponseEntity<RegisterForRafflesResponse> response = restTemplate.exchange(
                    BASE_URL + "/register",
                    HttpMethod.POST,
                    requestEntity,
                    RegisterForRafflesResponse.class
            );

            logger.info("Data sent successfully. Status: {}", response.getStatusCode());
            return response.getBody();

        } catch (Exception e) {
            logger.error("Failed to send data: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to process request: " + e.getMessage());
        }
    }

}
