FROM gradle:8.5-jdk21 AS builder

WORKDIR /build

COPY gradlew ./
COPY gradle/ ./gradle/
COPY build.gradle settings.gradle ./

RUN ./gradlew dependencies --no-daemon

COPY . .

RUN ./gradlew build --no-daemon -x test

FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

EXPOSE 8080

ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]