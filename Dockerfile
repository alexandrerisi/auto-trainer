FROM openjdk:11
COPY src /
CMD ["./mvnw", "spring-boot:run"]