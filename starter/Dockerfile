FROM amazoncorretto:17.0.8-alpine3.18 as starter-service
LABEL authors="hipp"

COPY target/*-spring-boot.jar service.jar
RUN chmod +x service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar" ,"service.jar"]