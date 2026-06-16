FROM gradle:9.5.1-jdk25-alpine AS build
RUN mkdir annotace
WORKDIR /annotace
COPY . .
RUN gradle bootJar -x test

FROM eclipse-temurin:25-jdk-alpine AS runtime
COPY --from=build /annotace/core/build/libs/*.jar /
RUN mv annotace*.jar annotace.jar

EXPOSE 8080
ENV JDK_JAVA_OPTIONS="--add-opens java.base/sun.nio.ch=ALL-UNNAMED java.base/java.nio=ALL-UNNAMED java.base/java.util=ALL-UNNAMED java.base/java.lang=ALL-UNNAMED java.base/java.lang.invoke=ALL-UNNAMED"
ENTRYPOINT ["java","-XX:+UseCompactObjectHeaders","-jar","/annotace.jar"]