FROM java:8-alpine
VOLUME /template_app
ADD /target/XtemplateX_app.jar app.jar
RUN sh -c 'touch app.jar'
EXPOSE template_port
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]
