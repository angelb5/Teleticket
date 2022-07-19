FROM openjdk:17.0.2-jdk
VOLUME /tmp
EXPOSE 8080
ADD ./target/Teleticket-0.0.1-SNAPSHOT.jar teleticket.jar
ENTRYPOINT ["java","-jar","teleticket.jar"]
ENV TZ America/Lima
