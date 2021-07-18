FROM alpine:3.14

MAINTAINER "Vladut Buhoci"

RUN apk update \
    && apk upgrade \
    && apk add openjdk8

RUN alias ll="ls -alh"
RUN mkdir /tetrisGame

COPY target/tetris_with_ai-*.jar /tetrisGame/tetrisWithAI.jar
COPY Aknowledgement.txt /tetrisGame/

CMD ["/usr/bin/java", "-cp", "/tetrisGame/tetrisWithAI.jar", "edu.vbu.tetris_with_ai.TetrisForMultipleGeneticAIs"]
