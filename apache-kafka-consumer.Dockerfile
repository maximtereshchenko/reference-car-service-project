FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /usr/src/app/
COPY ./ ./
RUN chmod +x gradlew
RUN ./gradlew -x test --no-daemon apache-kafka-consumer:installDist

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /usr/src/app/apache-kafka-consumer/build/install/apache-kafka-consumer /usr/app/
WORKDIR usr/app/bin
ENV JAVA_OPTS="-XX:MaxRAMPercentage=40"
ENTRYPOINT ./apache-kafka-consumer