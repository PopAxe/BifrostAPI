# syntax=docker/dockerfile:1

FROM gradle:jdk17 as BUILD_IMAGE
WORKDIR /app
COPY . /app
RUN gradle bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=BUILD_IMAGE /app/build/libs/bifrost-api.jar .
ENV STAGE=dev
EXPOSE 8443
RUN ls /
CMD [ "java", "-jar", "bifrost-api.jar" ]