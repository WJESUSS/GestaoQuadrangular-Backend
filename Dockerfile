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

ENTRYPOINT ["sh","-c","java -Xmx450m -Xms128m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:MaxMetaspaceSize=96m -Dserver.port=${PORT:-10000} -jar app.jar"]