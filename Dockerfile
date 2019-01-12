FROM maven:3.5.4-jdk-8-slim AS builder
ARG BUILD_NUMBER=undefined
ARG BUILD_TIMESTAMP=undefined
ARG GIT_COMMIT_SHORT=undefined
ARG GIT_AUTHOR_NAME=undefined

ENV BUILD_NUMBER ${BUILD_NUMBER}
ENV BUILD_TIMESTAMP ${BUILD_TIMESTAMP}
ENV GIT_COMMIT_SHORT ${GIT_COMMIT_SHORT}
ENV GIT_AUTHOR_NAME ${GIT_AUTHOR_NAME}

COPY . /usr/src/leanda
WORKDIR /usr/src/leanda
RUN mvn -B -DskipTests -pl '!worker-service-template' clean package
