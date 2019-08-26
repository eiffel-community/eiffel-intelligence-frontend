# Running Eiffel Intelligence front-end

## _Prerequisites_

Eiffel intelligence front-end is a Spring microservice distributed in a war file.

Eiffel Intelligence front-end release war files can be downloaded from Jitpack:
[jitpack.io](https://jitpack.io/#eiffel-community/eiffel-intelligence-frontend) and look for the
latest version. Now replace the latest version in the link below:

    https://jitpack.io/com/github/eiffel-community/eiffel-intelligence-frontend/<version>/eiffel-intelligence-<version>.war

## Running with maven command

If you want to test the latest code in GitHub clone the project and compile it
with:

    mvn clean install

append **_-DskipTests_** if you want to skip the tests since the latest on
master always has passed the tests. The war file should now be found under
target folder in your cloned project.

If you run from source code, you can run Eiffel Intelligence front-end with maven command:

    mvn spring-boot:run

## Running with java command

Another option is to run the executable war file with java command.
If running from source code, war file is generated and produced by maven command (mvn install command can be used as well):

    mvn package -DskipTests

This command should produce a eiffel-intelligence-frontend-<version>.war file in target folder, target/eiffel-intelligence-frontend-<version>.war.

War file is executed by following command and with default configuration:

    java -jar eiffel-intelligence-frontend-<version>.war

Own configuration can be provided with

    java -jar eiffel-intelligence-frontend-<version>.war --spring.config.location=file:<path to own application.properties>

remember to keep the name of the properties file if you are a beginner to
Spring. More advanced Spring user can look [here](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
for more information about using external configuration.

If only few properties need to be overriden, then use Java opts, for example

    java -jar eiffel-intelligence-frontend-<version>.war -Dspring.data.mongodb.port=27019


## Running in Tomcat instance

To run Eiffel Intelligence front-end in Tomcat, the war file must be put into the webapp folder in tomcat installation folder, also called catalina home folder:

    (catalina home)/webapp/

If Eiffel Intelligence front-end should be run without any context-path in the url address, then overwrite ROOT.war file in webapp folder with eiffel-intelligence-frontend-<version>.war file:

    cp eiffel-intelligence-frontend-<version>.war (catalina home)/webapp/ROOT.war

Remove "ROOT" folder in webapp folder:

    rm -rf (catalina home)/webapp/ROOT/

Create "config" folder in catalina home folder, if it doesn't exist. Spring and Eiffel Intelligence front-end will look for the application.properties configuration file in config folder:

    mkdir (catalina home)/config

Copy the application.propeties file into the newly created config folder:
    
    cp application.properties (catalina home)/config

Start Tomcat and Eiffel Intelligence front-end in background/daemon mode by executing command:

    (catalina home)/bin/catalina.sh start

To run Tomcat and Eiffel Intelligence front-end with logs printed to console:
    
    (catalina home)/bin/catalina.sh run

## Eiffel Intelligence front-end configurations and properties

All available Eiffel Intelligence front-end properties can be found in [application.properties](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/resources/application.properties) example file.

More documentation of each Eiffel Intelligence front-end property and configurations can be found in [Configuration page](https://github.com/eiffel-community/eiffel-intelligence-frontend/blob/master/wiki/markdown/configuration.md)
