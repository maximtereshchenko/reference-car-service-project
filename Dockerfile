FROM eclipse-temurin:17-jdk-alpine AS build
ARG module
WORKDIR /usr/src/app/
COPY ./ ./
RUN chmod +x gradlew
RUN ./gradlew -x test --no-daemon ${module}:installDist

FROM eclipse-temurin:17-jre-alpine
ARG module
COPY --from=build /usr/src/app/${module}/build/install/${module} /usr/app/
WORKDIR usr/app/bin
ENV ENTRYPOINT_SCRIPT=${module}
ENV JAVA_OPTS="-XX:MaxRAMPercentage=40"
EXPOSE 8080
ENTRYPOINT ./${ENTRYPOINT_SCRIPT}