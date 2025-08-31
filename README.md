# Introduction
This project `holiday-planner-api` is a demo Spring Boot 3.5.5 application that provides a RESTful API to fetch holiday data using the Nager Date API. 
The application includes endpoints to retrieve the last given number of holidays for a specified country, 
non-weekend holidays count for multiple countries in a given year, and find shared holidays between two countries in a specified year. 
The API is documented using Swagger annotations for easy understanding and testing.

# Holiday API Setup and Usage

## Setup
- Ensure you have InteliJ IDEA, JDK 17 and Maven installed and configured in your system.
- Clone this project on your local computer from https://github.com/pareshagrawal2025/holiday-planner-api.git.
- Optionally we can open the project in Eclipse or InteliJ IDEA after cloning to local.

## Build Application
- Open a terminal and navigate to the project root directory.
- Run `mvnw clean package` from the project root.
- Wait for the build to complete successfully.

## Check Test Reports
- After successful build, open file manager and navigate to the `{project root}/target/site/jacoco` directory.
- Open `index.html` present in this directory with web browser to check test coverage report.
- This report will show code coverage of unit tests. It is not 100% but good enough for this demo application.

## Run the Application
- If build is successful then we can run the application from terminal.
- Run `mvnw spring-boot:run` from the project root.
- The application will start on `http://localhost:8080`

## Run Application using Docker-Compose
- Ensure you have Docker and Docker Compose installed and configured in your system.
- After building application with `mvnw clean package` 
- run `docker-compose up` from the project root.

## Access Swagger UI
- Open `http://localhost:8080/swagger-ui/index.html` in a browser to view the interactive API documentation.
- We can test the endpoints directly from the Swagger UI.

## Health Checks
- Access `http://localhost:8080/management/health` to check the health status of the application.
- It should return `{"status":"UP"}` if the application is running correctly.
- We can also access info and prometheus endpoints like `/management/info`, `/management/prometheus`, etc.

## Test Endpoints
- **Last given number of Holidays**: `GET /api/holidays/last-number-of-holidays/NL?numberOfHolidays=3`
    - Returns the last number of given (default 3 max 12) holidays celebrated in the Netherlands (e.g., based on today’s date, August 31, 2025).
- **Non-Weekend Holiday Counts**: `GET /api/holidays/non-weekend/2025?countryCodes=NL,DE,FR`
    - Returns the count of non-weekend holidays for the NL, DE and FR in 2025, sorted by count.
- **Shared Holidays**: `GET /api/holidays/shared/2025/NL/DE`
    - Returns holidays celebrated on the same date in both the NL and DE, with local names.

## Notes
- The Nager Date API is used to fetch public holiday data. Ensure internet connectivity for API calls.
- The `holiday-planner-api` handles the logic for each requirement, including deduplication and sorting.
- Swagger annotations (`@Operation`, `@ApiResponses`, `@Parameter`, `@Tag`, `@Schema`) provide detailed documentation, making the API user-friendly. 
- The code assumes holidays are fetched in the format provided by Nager Date (date in `YYYY-MM-DD` and `localName`).
- Error handling is very good for application, validation added for country codes and year parameters.