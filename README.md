# Annotace

Annotace is a text analysis service used e.g. by [TermIt](https://github.com/kbss-cvut/termit) and its [web annotation plugin](https://github.com/alanbuzek/termit-extension).

## How to run it?

- Install Java 11
- Run `./gradlew bootRun` (on Linux/WSL) or `gradlew.bat bootRun` on Windows

## Lemmatizers

Annotace supports two lemmatizer implementations: 

- [Spark](https://sparknlp.org/)-based lemmatizer is more suitable for annotation of English texts. This is the default lemmatizer
- [MorphoDiTa](https://ufal.mff.cuni.cz/morphodita)-based lemmatizer is more suitable for annotation of Czech or Slovak texts. It comes in two variants:
  - JNI-based - runs locally using the MorphoDiTa library itself
  - Service-based - invokes a remote annotation service (needs to be configured)

## Setup

Spark-based Annotace setup does not require any additional configuration or files. Either run it directly `./gradlew bootRun`
or use Docker. There is an [image](ghcr.io/kbss-cvut/annotace/annotace-spark:latest) published at GitHub package registry.

Running Annotace with MorphoDiTa is a bit more complicated.

### Annotace with MorphoDiTa Locally

1. Download the MorphoDiTa [ZIP archive](https://github.com/kbss-cvut/annotace/pkgs/container/annotace%2Fannotace-spark) and extract it.
2. Find a file with JNI bindings corresponding to your platform in the extracted directory. For 64-bit Linux the file is `morphodita-1.9.2-bin/bin-linux64/java/libmorphodita_java.so`.
3. Set path to the **directory containing this file** as `java.library.path` environment variable name.
4. Provide mapping of taggers (language models) to Annotace. Either by editing `application.yml` before build or by passing them as environment variables.
5. Run Annotace with the MorphoDiTa lemmatizer by setting `ANNOTACE_LEMMATIZER` to `morphodita-jni`.

A complete command line example would be: 
`ANNOTACE_LEMMATIZER=morphodita-jni ANNOTACE_MORPHODITA_TAGGERS_CS=/opt/annotace/lib/czech-morfflex2.0-pdtc1.0-220710.tagger LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/annotace/lib/morphodita-1.9.2-bin/bin-linux64/java ./gradlew bootRun`


### Annotace with MorphoDiTa in Docker

1. Download the MorphoDiTa [ZIP archive](https://github.com/ufal/morphodita/releases/download/v1.9.2/morphodita-1.9.2-bin.zip).
2. Set `MORPHODITA_ZIP` in `docker-compose-morphodita.yml` to path to the downloaded MorphoDiTa ZIP file.
3. Download and extract taggers (language models). Put them into a single directory.
4. Set `MORPHODITA_TAGGERS` in `docker-compose-morphodita.yml` to path to the taggers' directory.
5. Run `docker compose -f docker-compose-morphodita.yml up -d --build` to build and start Annotace wih MorphoDiTa.

## License

Annotace is licensed under GPL v3.0, Spark and MorphoDiTa are distributed under their respective licenses.