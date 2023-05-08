FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /usr/src/app/
COPY ./ ./
RUN chmod +x gradlew
RUN ./gradlew -x test --no-daemon application:installDist

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /usr/src/app/application/build/install/application /usr/app/
WORKDIR usr/app/bin
ENV JAVA_OPTS="-XX:MaxRAMPercentage=40"
EXPOSE 8080
ENTRYPOINT ./application