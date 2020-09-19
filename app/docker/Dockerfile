FROM adoptopenjdk/openjdk11:latest
RUN mkdir /opt/app
COPY target/notification-services-app-*.jar /opt/app/notification-services-app.jar
CMD ["java", "-Xss10m", "-jar", "/opt/app/notification-services-app.jar"]
