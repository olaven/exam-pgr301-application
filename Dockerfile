FROM maven:latest
COPY target/geiger-1.0-SNAPSHOT.jar .
CMD java -jar ./geiger-1.0-SNAPSHOT.jar