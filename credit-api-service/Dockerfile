

FROM bellsoft/liberica-openjdk-alpine:23 AS builder
WORKDIR /build

COPY pom.xml .
COPY credit-api-service/pom.xml credit-api-service/
COPY credit-processing-service/pom.xml credit-processing-service/

RUN --mount=type=cache,target=/root/.m2 \
    apk add --no-cache maven && \
    mvn dependency:go-offline -q

COPY credit-api-service/src credit-api-service/src/
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -pl credit-api-service -am -DskipTests -q


FROM bellsoft/liberica-openjre-alpine:23 AS layers
WORKDIR /application

COPY --from=builder /build/credit-api-service/target/*.jar app.jar

RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted 2>/dev/null || \
    (mkdir -p extracted/dependencies extracted/application && cp app.jar extracted/application/)


FROM bellsoft/liberica-openjre-alpine:23
VOLUME /tmp

RUN addgroup -S spring && adduser -S spring-user -G spring && \
    mkdir -p /application /dumps /logs && \
    chown -R spring-user:spring /application /dumps /logs
USER spring-user

WORKDIR /application

COPY --from=layers --chown=spring-user:spring /application/extracted/dependencies/ ./
COPY --from=layers --chown=spring-user:spring /application/extracted/spring-boot-loader/ ./ 2>/dev/null || true
COPY --from=layers --chown=spring-user:spring /application/extracted/snapshot-dependencies/ ./ 2>/dev/null || true
COPY --from=layers --chown=spring-user:spring /application/extracted/application/ ./


ENV JAVA_TOOL_OPTIONS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps"

ENTRYPOINT ["java", "-jar", "app.jar"]