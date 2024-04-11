FROM gradle:8.0.2-jdk11-alpine as build
RUN mkdir annotace
WORKDIR /annotace
COPY . .
RUN gradle bootJar -x test

FROM eclipse-temurin:11-jdk-alpine as runtime
COPY --from=build /annotace/core/build/libs/*.jar /
RUN mv annotace*.jar annotace.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/annotace.jar"]