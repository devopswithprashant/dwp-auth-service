FROM eclipse-temurin:17-jre

LABEL maintainer="DevOps with Prashant"

WORKDIR /app

RUN addgroup --system spring && \
    adduser --system spring --ingroup spring

COPY build/libs/*.jar app.jar

RUN chown -R spring:spring /app

USER spring

EXPOSE 8081

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=local

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]