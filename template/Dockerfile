FROM gradle:jdk11 AS builder

COPY . /home/gradle/project

WORKDIR /home/gradle/project

RUN gradle bootJar

FROM bellsoft/liberica-openjdk-alpine-musl:11

LABEL maintainer="Async API (https://www.asyncapi.com/about/)"

USER 1000

ARG PROFILE=default
ARG JAR_FILE=/home/gradle/project/build/libs/*.jar

ENV spring_profiles_active=${PROFILE}

COPY --from=builder ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
