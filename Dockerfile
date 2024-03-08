FROM openjdk:17-alpine

ADD /build/libs/api-news-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/api-news-0.0.1-SNAPSHOT.jar"]

EXPOSE 8083
