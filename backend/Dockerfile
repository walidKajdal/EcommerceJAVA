  # Use the official Tomcat image as the base
  FROM tomcat:9.0

  # Set the working directory
  WORKDIR /usr/local/tomcat/webapps/

  # Copy your WAR file to the webapps directory
  COPY your-app.war ./ROOT.war

  # Expose port 8080
  EXPOSE 8080

  # Start Tomcat
  CMD ["catalina.sh", "run"]
