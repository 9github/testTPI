FROM alpine/java:21-jdk
MAINTAINER peter.com
COPY build/libs/urlshorterner-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 9170
ENTRYPOINT ["java","-jar","./app.jar"]
