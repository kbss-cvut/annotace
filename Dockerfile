FROM gradle:8.7.0-jdk17 AS build
RUN mkdir annotace
WORKDIR /annotace
COPY . .
RUN gradle :core:quarkusBuild -x test

FROM eclipse-temurin:17-jdk
WORKDIR /annotace
COPY --from=build /annotace/core/build/quarkus-app/lib/      /annotace/lib/
COPY --from=build /annotace/core/build/quarkus-app/*.jar     /annotace/
COPY --from=build /annotace/core/build/quarkus-app/app/      /annotace/app/
COPY --from=build /annotace/core/build/quarkus-app/quarkus/  /annotace/quarkus/

EXPOSE 8080
ENTRYPOINT ["java","-jar","/annotace/quarkus-run.jar"]