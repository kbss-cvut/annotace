ARG MORPHODITA_MODEL
ARG MORPHODITA_ZIP
ARG MORPHODITA_ZIP_SO

########################################################################################################################

FROM openjdk:11 as unzip
ARG MORPHODITA_MODEL
ARG MORPHODITA_ZIP
COPY $MORPHODITA_MODEL .
RUN unzip $MORPHODITA_MODEL
COPY $MORPHODITA_ZIP .
RUN unzip $MORPHODITA_ZIP

FROM maven:3.8.1-jdk-11 as buildMaven
ARG MORPHODITA_MODEL_TAGGER_FILE
ARG MORPHODITA_ZIP_SO
RUN mkdir annotace
WORKDIR /annotace
COPY . .
COPY --from=unzip $MORPHODITA_MODEL_TAGGER_FILE /tagger
COPY --from=unzip $MORPHODITA_ZIP_SO /lib
RUN mvn -Dannotace.morphodita.tagger=/tagger test
RUN mvn -DskipTests=true package

FROM openjdk:11 as runtime
ARG MORPHODITA_MODEL_TAGGER_FILE
ARG MORPHODITA_ZIP_SO
COPY --from=buildMaven /annotace/target/*.jar /
RUN ls -ltr /
RUN mv *.jar annotace.jar
COPY --from=unzip $MORPHODITA_MODEL_TAGGER_FILE /
COPY --from=unzip $MORPHODITA_ZIP_SO /lib

EXPOSE 8080
ENTRYPOINT ["java","-jar","/annotace.jar"]