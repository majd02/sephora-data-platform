# Étape 1 : Build
FROM maven:3.8-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ src/
# Maven va créer le Fat JAR ici
RUN mvn clean package -DskipTests

# Étape 2 : Run
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app

# On récupère le JAR (le plugin Shade crée souvent un fichier nommé 'sephora-data-platform-1.0-SNAPSHOT.jar')
COPY --from=build /app/target/*.jar app.jar

# On lance le JAR normalement avec -jar car le Manifest est géré par le plugin ou on garde le -cp
ENTRYPOINT ["java", "-cp", "app.jar", "com.sephora.data.Main"]