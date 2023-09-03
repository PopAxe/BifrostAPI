# syntax=docker/dockerfile:1

FROM gradle:jdk17 as BUILD_IMAGE
WORKDIR /app
COPY . /app
RUN gradle bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=BUILD_IMAGE /app/build/libs/bifrost-api.jar .
COPY --from=BUILD_IMAGE /app/certs /app/certs
ENV STAGE=prod
EXPOSE 8443
# We empty entrypoint because the parent image causes failures trying to execute its script, which we shouldn't need
ENTRYPOINT []
CMD [ "java", "-jar", "bifrost-api.jar" ]