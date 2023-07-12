FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /

COPY ./build/libs/app-0.0.1-SNAPSHOT.jar /sg.jar

EXPOSE 3000

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", \
    "-jar", "/sg.jar"]