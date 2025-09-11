FROM amazoncorretto:24-headless
LABEL authors="hipp"
COPY target/*-spring-boot.jar service.jar
RUN chmod +x service.jar
ENTRYPOINT ["java","-jar" ,"service.jar"]