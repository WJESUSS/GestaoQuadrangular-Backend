# -------- Stage 1: Build --------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia pom.xml
COPY pom.xml .

RUN mvn dependency:go-offline

# 🔥 CORREÇÃO AQUI
COPY . .

# Build
RUN mvn clean package -DskipTests


# -------- Stage 2: Runtime --------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# 🔥 PORTA DO RENDER
ENTRYPOINT ["sh","-c","java -Dserver.port=$PORT -jar app.jar"]