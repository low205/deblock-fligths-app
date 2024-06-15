FROM gradle:8.8-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM eclipse-temurin:21
EXPOSE 8080:8080
RUN mkdir /opt/app
COPY --from=build /home/gradle/src/flights/app/build/libs/app-1.0-SNAPSHOT-all.jar /opt/app/app.jar
COPY --from=build /home/gradle/src/config.yml /opt/app/config.yml
ENTRYPOINT ["java","-jar","/opt/app/app.jar", "/opt/app/config.yml"]