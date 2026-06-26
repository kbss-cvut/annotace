FROM gradle:9.5.1-jdk21-alpine AS build
RUN mkdir annotace
WORKDIR /annotace
COPY . .
RUN gradle bootJar -x test

FROM eclipse-temurin:21-jdk-alpine AS runtime
# Run as a non-root user. Spark NLP downloads pretrained models on startup into
# its cache folder (defaults to ~/cache_pretrained), and Spark writes runtime
# artifacts (metastore_db, spark-warehouse) into the working directory, so the
# user needs a writable home and working directory.
RUN addgroup -S annotace \
    && adduser -S -G annotace -h /home/annotace annotace \
    && mkdir -p /app \
    && chown -R annotace:annotace /app /home/annotace
WORKDIR /app
COPY --from=build --chown=annotace:annotace /annotace/core/build/libs/annotace-*.jar /app/annotace.jar

EXPOSE 8080
ENV HOME=/home/annotace
ENV JDK_JAVA_OPTIONS="--add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED"
USER annotace
ENTRYPOINT ["java","-jar","/app/annotace.jar"]