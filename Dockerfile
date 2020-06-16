#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml test
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/live-noot.jar /usr/local/lib/live-noot.jar
EXPOSE 8041
ENTRYPOINT ["java","-jar","/usr/local/lib/live-noot.jar"]
