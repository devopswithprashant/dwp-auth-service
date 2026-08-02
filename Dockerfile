# ==========================================
# STAGE 1: Build Alpine-Native Custom JRE
# ==========================================
FROM alpine:3.20 AS jlink-builder

# Install JDK 17 and binutils (provides 'objcopy' required for --strip-debug)
RUN apk add --no-cache openjdk17-jdk binutils

RUN /usr/lib/jvm/java-17-openjdk/bin/jlink \
    --add-modules java.base,java.desktop,java.instrument,java.management,java.naming,java.net.http,java.security.jgss,java.security.sasl,java.sql,java.transaction.xa,java.xml,jdk.unsupported,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /custom-jre

# ==========================================
# STAGE 2: Ultra-Lean Alpine Runtime
# ==========================================
FROM alpine:3.20

LABEL maintainer="DevOps with Prashant"

# Install timezone data and C++ standard library needed by Java
RUN apk add --no-cache tzdata libstdc++

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copy custom Alpine-native JRE built in Stage 1
COPY --from=jlink-builder /custom-jre $JAVA_HOME

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy application JAR directly with ownership
COPY --chown=spring:spring build/libs/*.jar app.jar

USER spring

EXPOSE 8081

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=local

ENTRYPOINT ["java", "-jar", "app.jar"]