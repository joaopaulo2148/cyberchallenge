# ---------------------------------------------------------
# CYBER CHALLENGE - Dockerfile (build + execucao)
# ---------------------------------------------------------

# Etapa 1: compila o projeto com Maven (nao depende da pasta target/)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# Etapa 2: imagem final, so com o JRE (menor e mais leve)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Limites de memoria para caber nos 512 MB do plano gratuito da Render
ENV JAVA_TOOL_OPTIONS="-Xmx300m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC"

EXPOSE 8080

# - server.port: usa a porta que a plataforma informa em PORT (padrao 8080)
# - forward-headers-strategy: reconhece o HTTPS entregue pelo proxy da Render
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dserver.forward-headers-strategy=framework -jar app.jar"]
