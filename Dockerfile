FROM openjdk:11
COPY . /
CMD ["./mvnw", "spring-boot:run"]