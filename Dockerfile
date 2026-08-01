# ==========================================
# STAGE 1: Build Custom Minimal JRE
# Uses standard Eclipse Temurin JDK
# ==========================================
FROM eclipse-temurin:17-jdk AS jlink-builder

# Create minimal JRE including java.desktop for Spring Boot compatibility
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.desktop,java.instrument,java.management,java.naming,java.net.http,java.security.jgss,java.security.sasl,java.sql,java.transaction.xa,java.xml,jdk.unsupported,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /custom-jre

# ==========================================
# STAGE 2: Debian-Slim Production Runtime (~135 MB)
# ==========================================
FROM debian:bookworm-slim

LABEL maintainer="DevOps with Prashant"

# Install tzdata for timezone management & clean apt cache
RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copy custom JRE from Stage 1
COPY --from=jlink-builder /custom-jre $JAVA_HOME

WORKDIR /app

# Create non-root user
RUN groupadd --system spring && useradd --system spring -g spring

# Copy JAR with permissions set
COPY --chown=spring:spring build/libs/*.jar app.jar

USER spring

EXPOSE 8081

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=local

ENTRYPOINT ["java", "-jar", "app.jar"]