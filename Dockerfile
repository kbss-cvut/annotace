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

FROM gradle:7.1.1-jdk11 as buildMaven
ARG MORPHODITA_MODEL_TAGGER_FILE
ARG MORPHODITA_ZIP_SO
RUN mkdir annotace
WORKDIR /annotace
COPY . .
COPY --from=unzip $MORPHODITA_MODEL_TAGGER_FILE /tagger
COPY --from=unzip $MORPHODITA_ZIP_SO /lib
RUN ANNOTACE_MORPHODITA_TAGGER=/tagger gradle test
RUN gradle bootJar

FROM openjdk:11 as runtime
ARG MORPHODITA_MODEL_TAGGER_FILE
ARG MORPHODITA_ZIP_SO
COPY --from=buildMaven /annotace/core/build/libs/*.jar /
RUN mv *.jar annotace.jar
COPY --from=unzip $MORPHODITA_MODEL_TAGGER_FILE /
COPY --from=unzip $MORPHODITA_ZIP_SO /lib

EXPOSE 8080
ENTRYPOINT ["java","-jar","/annotace.jar"]