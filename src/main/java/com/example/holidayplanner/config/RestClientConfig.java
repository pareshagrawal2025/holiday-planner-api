package com.example.holidayplanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration class to define RestClient bean for making REST API calls towards Nager Date API.
 * <p>
 * This class provides a Spring bean for RestClient, which is used to perform HTTP requests to external services.
 */
@Configuration
public class RestClientConfig {

    /**
     * Defines a RestClient bean for making REST API calls.
     * <p>
     * This bean is used to interact with the Nager Date API from other service classes.
     *
     * @return a new instance of RestClient
     */
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
