FROM openjdk:11
COPY src /
RUN ["mvnw", "spring-boot:run"]