# The first image will be used to build the app from source files.
FROM maven:3.8.6-openjdk-8-slim as builder

MAINTAINER "Vladut Buhoci"

WORKDIR /tetrisGameSrc

COPY ./ ./

RUN mvn package -P dev

# The final image will contain the executable binaries.
FROM alpine:3.14

MAINTAINER "Vladut Buhoci"

RUN apk update \
    && apk upgrade \
    && apk add openjdk8

WORKDIR /tetrisGame

COPY --from=builder /tetrisGameSrc/target/tetris_with_ai-*.jar ./tetrisWithAI.jar
COPY --from=builder /tetrisGameSrc/Aknowledgement.txt ./

CMD ["/usr/bin/java", "-cp", "tetrisWithAI.jar", "edu.vbu.tetris_with_ai.TetrisForMultipleGeneticAIs"]
