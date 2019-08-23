# Running Eiffel Intelligence front-end

## _Prerequisites_

Eiffel intelligence front-end is a Spring microservice distributed in a war file.

Release Eiffel Interlligence front-end war files can be downloaded from Jitpack:
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

If you run from source code, you can run Eiffel-Intelligence front-end with maven command:

    mvn spring-boot:run

## Running with java command

Another opthion is to run the executable war file (located in target folder, if running from source code):

    java -jar eiffel-intelligence-frontend-<version>.war

if you want to run with default configuration.

Own configuration can be provided with

    java -jar eiffel-intelligence-frontend-<version>.war --spring.config.location=file:<path to own application.properties>

remember to keep the name of the properties file if you are a beginner to
Spring. More advanced Spring user can look [here](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
for more information about using external configuration.

If only few properties need to be overriden then use Java opts, for example

    java -jar eiffel-intelligence-frontend-<version>.war -Dspring.data.mongodb.port=27019


## Running in Tomcat instance

To run Eiffel-Intelligence front-end in Tomcat, the war file most be put into the webapp folder in tomcat installation folder, also called catalina home folder:

    (catalina home)/webapp/

If Eiffel-Intelligence front-end should be run without any conext-path in the url address, then overwrite ROOT.war file in webapp file with eiffel-intelligence-frontend-<version>.war:

    cp eiffel-intelligence-frontend-<version>.war (catalina home)/webapp/ROOT.war

Remove "ROOT" folder in webapp folder:

    rm -rf (catalina home)/webapp/ROOT/

Create "config" folder in webapp folder, if it not exists. Spring and Eiffel-Intelligence front-end will look for the applications.properties configuration file from this config folder:

    mkdir (catalina home)/webapp/config

Copy the application.propeties file into the newly created config folder:
    
    cp applications.properties (catalina home)/webapp/config

Start Tomcat and Eiffel-Intelligence front-end in background/daemon mode by exectuing command:

    (catalina home)/bin/catalina.sh start

To run Tomcat and Eiffel-Intelligence front-end with logs printed to console:
    
    catalina home)/bin/catalina.sh start

## Eiffel-Intelligence fron-tend configurations and properties

All available Eiffel-Intelligence front-end properties can be found in [application.properties](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/resources/application.properties) example file.

More documentation of each Eiffel-Intelligence front-end properties and configuration can be found in [Configuration page](./configuration.md)