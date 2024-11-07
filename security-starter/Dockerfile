FROM amazoncorretto:22-alpine3.20
LABEL authors="hipp"

COPY target/*-spring-boot.jar service.jar
RUN chmod +x service.jar
EXPOSE 8080:8080
ENTRYPOINT ["java","-jar" ,"service.jar"]