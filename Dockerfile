FROM gradle:7.1.1-jdk11 as build
RUN mkdir annotace
WORKDIR /annotace
COPY . .
RUN gradle bootJar -Pcore,lemmatizer-spark,keywordextractor-ker

FROM openjdk:11 as runtime
COPY --from=build /annotace/core/build/libs/*.jar /
RUN mv annotace*.jar annotace.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/annotace.jar"]