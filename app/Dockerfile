FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /

COPY ./build/libs/app-0.0.1-SNAPSHOT.jar /sg.jar

# Deployment with frontend baked in
# Copy the resources from the host into the image at /build/resources
# COPY ./build/resources /build/resources

EXPOSE 2000

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", \
    "-jar", "/sg.jar"]