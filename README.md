# Introduction
This project `holiday-planner-api` is a demo Spring Boot application that provides a RESTful API to fetch holiday data using the Nager Date API. 
The application includes endpoints to retrieve the last given number of holidays for a specified country, 
non-weekend holidays count for multiple countries in a given year, and find shared holidays between two countries in a specified year. 
The API is documented using Swagger annotations for easy understanding and testing.
Developer followed Test Driven Development (TDD) approach to develop this application.

# Holiday Planner API Setup and Usage

## Api Specification
- Application Api specification is available under  `src/main/resources/open-api/open-api-specifications.yaml`
- Optionally we can get specification from `http://localhost:8080/api-docs` after starting the application,
or we can check it from swagger-ui page `http://localhost:8080/swagger-ui/index.html` when application is running.
- We are generating Api interface and models java code from specification given in `open-api-specifications.yaml` using `openapi-generator-maven-plugin`,
while building application with `mvnw clean package` command, code will be generated in `target/generated-sources/openapi/src/main/java` directory.

## Setup
- Ensure you have JDK 17 and Maven installed and configured in your system.
- Clone this project on your local computer from https://github.com/pareshagrawal2025/holiday-planner-api.git.
- Optionally we can open the project in Eclipse or InteliJ IDEA or any code editor after cloning to local.

## Build Application
- Open a terminal and navigate to the project root directory.
- Run `mvnw clean package` from the project root.
- Wait for the build to complete successfully.

## API Java Docs
- Java docs are generated using `maven-javadoc-plugin` while building application with `mvnw clean package` command.
- After successful build, open file manager and navigate to the `{project root}/target/javadocs/apidocs` directory.
- Open `index.html` present in this directory with web browser to check API java docs.
- API javadoc jar is also available as in `{project root}/target` directory as `generated-${project name}-${project version}-javadoc.jar`

## Test Plan and Performance Test
- Test plan is available in `docs` directory as `TestDesignDocument.md` file.
- Performance test is done using Apache JMeter 4.0 tool.
- JMeter test script available inside `performance_test` directory as `api-load-test.jmx` file.
- Application is load tested for 5 minute with 5 users, 150 request per seconds with ramp-up period 1 second.

## Check Test Reports
- After successful build, open file manager and navigate to the `{project root}/target/site/jacoco` directory.
- Open `index.html` present in this directory with web browser to check test coverage report.
- This report will show code coverage of unit tests. 95% code coverage achieved for application with zero sonar security issue.

## Run the Application
- If build is successful then we can run the application from terminal.
- Run `mvnw spring-boot:run` from the project root.
- The application will start on `http://localhost:8080`

## Run Application using Docker-Compose
- Ensure you have Docker installed and configured in your system.
- After building application with `mvnw clean package` 
- run `docker compose up -d` from the project root directory. It should build docker image and start the container.
- Optionally we can run `sh build_docker.sh` to build image with tag 2.1.0. It will build image `holidayplanner.example.com/api/holiday-planner-api:2.1.0`
- We can change in docker-compose.yml file to use this newly created image instead of building at startup of `docker compose up -d`. Just comment (#) line 7,8,9 and uncomment line 6.

## Access Swagger UI
- Open `http://localhost:8080/swagger-ui/index.html` in web browser to view the interactive API documentation.
- We can test the endpoints directly from the Swagger UI.

## Health Checks
- Access `http://localhost:8080/management/health` to check the health status of the application.
- It should return `{"status":"UP"}` if the application is running correctly.
- We can also access info and prometheus endpoints like `/management/info`, `/management/prometheus`, etc.

## API Endpoints
| Service Name                  | Endpoint                                                          | Description                                                                                        |
|-------------------------------|-------------------------------------------------------------------|----------------------------------------------------------------------------------------------------|
| Last given number of holidays | `GET /api/holidays/last-number-of-holidays/NL?numberOfHolidays=3` | Returns the last number of given (default 3, max 12) holidays celebrated in the Netherlands.       |
| Non-Weekend holiday counts    | `GET /api/holidays/non-weekend/2025?countryCodes=NL,DE,FR`        | Returns the count of non-weekend holidays for NL, DE, and FR in 2025, sorted by count high to low. |
| Shared holidays               | `GET /api/holidays/shared/2025/NL/DE`                             | Returns holidays celebrated on the same date in both countries NL and DE, with local names.        |
| Health check                  | `GET /management/info` or `GET /management/health`                | Returns application info or health check status.                                                   |
| Prometheus Health check       | `GET /management/prometheus`                                      | Returns application prometheus health parameters.                                                  |



## Developer Notes
- Frameworks used in application are, spring-boot 3.5.5 as base framework, caffeine for caching, springdoc-openapi-starter-webmvc-ui for API docs,
  openapi-generator, spring-boot-starter-test and jacoco for testing, maven-javadoc-plugin for java docs and some other util api 
  and Java 17 are used for the application.
- Error handling is appropriate in application, validation added for input parameters like country codes and year parameters.
- Application collects all user input errors in one go and returns to user instead of failing on first error.
- Performance is improved with caching of API responses using Caffeine cache.
- Unit tests are provided for application to ensure the correctness of the business logic.
- The Nager Date API is used to fetch public holiday data. Ensure internet connectivity available for API calls.
- The `holiday-planner-api` handles the logic for each requirement, including deduplication and sorting.
- Swagger annotations (`@Operation`, `@ApiResponses`, `@Parameter`, `@Tag`, `@Schema`) provide detailed documentation, making the API user-friendly.
- The code assumes holidays are fetched in the format provided by Nager Date (date in `YYYY-MM-DD` and `localName`).

# Issues
- Swagger UI is not taking example values in text fields and response schemas. The issue might be related to a bug in springdoc-openapi.
- This doesn't impact functionality, but it would be good to have example values in text fields for better user experience.
- We can manually enter values in text fields via configuration but that doesn't look good so skipped it for now.