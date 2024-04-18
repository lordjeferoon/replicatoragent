# syntax=docker/dockerfile:1
FROM eclipse-temurin:8-jre-jammy
RUN apt-get update -y && apt-get clean && apt-get install dumb-init tzdata -y
RUN groupadd -g 1004 hacom
RUN useradd -u 1004 -g 1004 hacom
WORKDIR /opt/app
VOLUME ["/opt/app/data/", "/opt/app/logs/"]
ARG JAR_FILE
COPY GatewayPublicWarningSystem-0.0.23.jar /opt/app/app.jar
RUN chown -R hacom:hacom /opt/app
USER hacom:hacom
CMD dumb-init java -Dlog4j.configurationFile=/opt/app/data/configs/log4j2.xml -Dfile.encoding=UTF-8 -Xms${JVM_XMS} -Xmx${JVM_XMX} -Dspring.config.location=/opt/app/data/configs/config.yml -server -jar app.jar
