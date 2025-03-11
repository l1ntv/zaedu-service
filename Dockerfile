FROM eclipse-temurin:23-jdk-alpine AS build
# Установка Maven
RUN apk update && apk add maven

COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean package -DskipTests # -DskipTests пропускает выполнение unit-тестов

FROM openjdk:23-slim

# Установка часового пояса (Moscow Time)
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Создание директории приложения
RUN mkdir /app
COPY --from=build /usr/src/app/target/*.jar /app/spring-boot-application.jar

# Оптимизация JVM-параметров для контейнера
# -XX:MaxRAMPercentage=75.0: Использование 75% доступной памяти контейнера
# -Duser.timezone=Europe/Moscow: Установка часового пояса для JVM
ENTRYPOINT ["java", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Duser.timezone=Europe/Moscow", \
    "-jar", "/app/spring-boot-application.jar"]