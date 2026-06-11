FROM gradle:9.5.1-jdk21-alpine AS build
RUN mkdir annotace
WORKDIR /annotace
COPY . .
RUN gradle :core:quarkusBuild -x test

FROM eclipse-temurin:25-jdk-alpine
WORKDIR /annotace
COPY --from=build /annotace/core/build/quarkus-app/lib/      /annotace/lib/
COPY --from=build /annotace/core/build/quarkus-app/*.jar     /annotace/
COPY --from=build /annotace/core/build/quarkus-app/app/      /annotace/app/
COPY --from=build /annotace/core/build/quarkus-app/quarkus/  /annotace/quarkus/

EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseCompactObjectHeaders","-jar","/annotace/quarkus-run.jar"]