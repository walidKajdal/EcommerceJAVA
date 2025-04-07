
FROM tomcat:10.1-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*


COPY target/backend-0.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
