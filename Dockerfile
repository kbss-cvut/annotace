FROM gradle:8.0.2-jdk11-alpine AS build
RUN mkdir annotace
WORKDIR /annotace
COPY . .
RUN gradle bootJar -x test

FROM eclipse-temurin:11-jdk-alpine AS runtime
COPY --from=build /annotace/core/build/libs/*.jar /
RUN mv annotace*.jar annotace.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/annotace.jar"]