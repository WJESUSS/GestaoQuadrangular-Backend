# -------- Stage 1: Build --------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# -------- Stage 2: Runtime --------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 10000

ENTRYPOINT ["sh","-c","java -Xmx384m -Xms64m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m -Dserver.port=${PORT:-10000} -jar app.jar"]