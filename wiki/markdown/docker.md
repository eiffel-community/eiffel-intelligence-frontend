# Docker

In Eiffel-Intelligence frontend source code repository, a Dockerfile is provided which helps the developer or user to build the local Eiffel-Intellegence frontend source code repository changes to a Docker image.
With the Docker image user can try-out the Eiffel-Intelligence frontend on a Docker Host or in a Kubernetes cluster.

## Requirements
- Docker


  Linux: https://docs.docker.com/install/linux/docker-ce/ubuntu/


  Windows: https://docs.docker.com/docker-for-windows/install/

- Docker Compose
  
  Linux and Windows:  https://docs.docker.com/compose/install/

## Follow these step to build the Docker image.

1. Build the Eiffel-Intelligence frontend war file:

`mvn package -DskipTests`


This will produce a war file in the "target" folder.

2. Build the Docker image with the war file that was produced from previous step:

`docker build -t eiffel-intelligence-frontend:0.0.19 --build-arg URL=./target/eiffel-intelligence-frontend-0.0.19.war -f src/main/docker/Dockerfile .` 

Now docker image has build with tag "eiffel-intelligence-frontend:0.0.19"

## Run Docker image on local Docker Host
To run the produced docker image on the local Docker host, execute this command:


`docker run -p 8071:8080 --expose 8080 -e server.port=8080 -e logging.level.log.level.root=DEBUG -e logging.level.org.springframework.web=DEBUG -e logging.level.com.ericsson.ei=DEBUG eiffel-intelligence-frontend:0.0.19`

# Some info of all flags to this command


## Eiffel Intelligence Spring Properties


<B>"-e server.port=8080"</B> - Is the Spring property setting for Eiffel-Intelligence applications web port.


<B>"-e logging.level.root=DEBUG -e logging.level.org.springframework.web=DEBUG -e
logging.level.com.ericsson.ei=DEBUG"</B> - These Spring properties set the logging level for the Eiffel-Intelligence applications.


It is possible to set all Spring available properties via docker environment "-e" flag. See the application.properties file for all available Eiffel-Intelligence Spring properties.


[application.properties](https://github.com/Ericsson/eiffel-intelligence/blob/master/src/main/resources/application.properties)


## Docker flags


<B>"--expose 8080"</B> - this Docker flag tells that containers internal port shall be exposed to outside of the Docker Host. This flag do not set which port that should be allocated outside Docker Host on the actual server/machine.


<B>"-p 8071:8080"</B> - this Docker flag is mapping the containers external port 8071 to the internal exposed port 8080. Port 8071 will be allocated outside Docker host and user will be able to access the containers service via port 8071.


When Eiffel-Intelligence container is running on your local Docker host Eiffel-Intelligence should be reachable with address "localhost:8071/\<Rest End-Point\>" or "\<docker host ip\>:8071/\<Rest End-Point\>"


In web-browser use url with docker host ip number: "\<docker host ip\>:8071/"

Switch-backend functionality do not work when "localhost" address is used.

Another option to configure Eiffel-Intelligence Front-end is to provide the application properties file into the container, which can be made in two ways:
1. Put application.properties file in Tomcat Catalina config folder in container and run Eiffe-Intelligence Front-end:

`docker run -p 8070:8080 --expose 8080 --volume /path/to/application.properties:/usr/local/tomcat/config/application.properties eiffel-intelligence-frontend:0.0.19`

2. Put application.properties file in a different folder in container and tell EI where the application.properties is located in the container:

`docker run -p 8070:8080 --expose 8080 --volume /path/to/application.properties:/tmp/application.properties -e spring.config.location=/tmp/application.properties eiffel-intelligence-frontend:0.0.19`


# Run Docker image with provided docker-compose file
This docker-compose file includes these components, [docker-compose.yml](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/docker/docker-compose.yml):
- MongoDb
- RabbitMq
- ER
- EI-Backend
- EI-Frontend (Using the local EI-Frontend Docker image build from previous steps)

If you have used a different image tag when you build the EI Frontend docker image, then you need to update docker-compose.yml file.

This line need to changed, in ei_backend service section:

"image: eiffel-intelligence-frontend:0.0.19"

To:

"image: \<your image tag\>"

Two variables need to be before we can start up all services with docker-compose tool.
Set Docker host ip to the HOST variable. 
If on Linux:

`export HOST=$(hostname -I | tr " " "\n"| head -1)`

If on Windows, get Docker Host ip with command: `dockermachine ip`

Set that Docker ip to HOST environment varaible.

Currently we need to provide EI Back-end instances list outside of docker-compose.yml file.

`export EIFFEL2_EI_FRONTEND_EI_INSTANCES_LIST="[{ "contextPath": "", "port": "8090", "name": "EI-Backend-1", "host": "localhost", "https": false, "defaultBackend": true}]"
`

Then run following docker-compose command to startup all components:

`docker-compose -f src/main/docker/docker-compose.yml up -d`

It will take some minutes until all components has started. When all components has loaded, you should be able to access EI Front-end web page with address:
http://localhost:8081/

Curl command can be used to make request via EI Front-end bridge to EI Back-end Rest-Api, example for getting all subscriptions:


`curl -X GET http://localhost:8081/subscriptions`

It is also possible to access these Rest-Api addresses in web-browser and get result presented in a Json view in web-browser.

Following command can be used to get the logs from the EI Front-end container/service:

`docker-compose -f src/main/docker/docker-compose.yml logs ei_frontend`

All service names can be retreived with following command:

`docker-compose -f src/main/docker/docker-compose.yml config --services`

It is also possible to retrieve the logs by only using "docker logs <container_id or container_name>" command:

`docker logs <container_id or container_name>`

Container id can be retrieved with docker command:

`docker ps`