FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/cw-0.0.1-SNAPSHOT.jar /app/app.jar
COPY src/main/resources/static/productImages /app/src/main/resources/static/productImages
COPY db.mv.db /app/db.mv.db

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]