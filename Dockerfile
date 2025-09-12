FROM amazoncorretto:24-headless
LABEL authors="hipp"
COPY target/*.jar service.jar
RUN chmod +x service.jar
ENTRYPOINT ["java","-jar" ,"service.jar","--Denable-native-access=ALL-UNNAMED"]