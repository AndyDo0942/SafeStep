FROM gradle:9.3.1-jdk25 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src src
RUN gradle clean bootJar -x test --no-daemon

FROM amazoncorretto:25
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
