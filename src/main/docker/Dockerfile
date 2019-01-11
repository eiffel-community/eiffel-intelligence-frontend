FROM fabric8/java-jboss-openjdk8-jdk:1.2
MAINTAINER Eiffel-Community
USER root
ENV JAVA_APP_DIR=/deployments
ENV JAVA_APP_JAR=eiffel-intelligence-frontend.war
EXPOSE 8092 8778 9779

# Create image with existing war file. User need to execute 'mvn package -DskipTest' before 'docker build'
COPY ./target/eiffel-intelligence-frontend-*.war /deployments/eiffel-intelligence-frontend.war

CMD /deployments/run-java.sh


