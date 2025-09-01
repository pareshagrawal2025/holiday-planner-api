# Dockerfile for building docker image for this spring Boot application using Amazon Corretto 17 JRE on Alpine Linux
# Use a alpine 3.21 based image from amazon corretto with Java 17
FROM amazoncorretto:17.0.15-alpine3.21

# Responsible party for this image
LABEL maintainer="example company"

# Install curl timezone and create a non-root user and group with specific IDs
RUN apk --update --no-cache add curl tzdata && \
    addgroup --system --gid 990 testuser && \
    adduser --disabled-password --gecos "" --home "$(pwd)" --ingroup testuser --no-create-home --uid 990 testuser

# Switch to the non-root user to run the application without root privileges
USER 990

# Copy the application JAR file to the container
COPY target/holiday-planner-api-*.jar /opt/holiday-planner-api.jar

# Set Java options and application options as environment variables
ENV JAVA_OPTS="-Xms128m -Xmx512m"
ENV APP_OPTS=""

# Define the entry point to run the Java application with the specified options
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/holiday-planner-api.jar $APP_OPTS" ]
