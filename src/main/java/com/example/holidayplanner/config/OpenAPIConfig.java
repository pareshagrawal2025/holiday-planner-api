package com.example.holidayplanner.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Open API configuration class to set up API metadata and server information
@Configuration
public class OpenAPIConfig {

    @Value("${holidayapi.openapi.dev-url}")
    private String openApiDevUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(openApiDevUrl);
        devServer.setDescription("Development server");


        Server productionServer = new Server();
        productionServer.setUrl("/");
        productionServer.setDescription("Production server");

        Contact contact = new Contact()
                .name("Holiday Planner Team")
                .email("support@holidayplanner.example.com")
                .url("https://holidayplanner.example.com");

        License license = new License()
                .name("Apache 2 License")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("Holiday Information API")
                .version("2.0.0")
                .description("API for retrieving holiday information using Nager Date API")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, productionServer));
    }
}