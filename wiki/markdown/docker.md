# Docker

In Eiffel-Intelligence front-end source code repository, a Dockerfile is provided which helps the developer or user to build the local Eiffel-Intellegence front-end source code repository changes to a Docker image.
With the Docker image user can try-out the Eiffel-Intelligence front-end on a Docker Host or in a Kubernetes cluster.

## Requirements
- Docker


  Linux: https://docs.docker.com/install/linux/docker-ce/ubuntu/


  Windows: https://docs.docker.com/docker-for-windows/install/

- Docker Compose

  Linux and Windows:  https://docs.docker.com/compose/install/

## Follow these step to build the Docker image.

1. Build the Eiffel-Intelligence front-end war file:

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

Switch-back-end functionality do not work when "localhost" address is used.

Another option to configure Eiffel Intelligence front-end is to provide the application properties file into the container, which can be made in two ways:
1. Put application.properties file in Tomcat Catalina config folder in container and run Eiffel Intelligence front-end:

`docker run -p 8070:8080 --expose 8080 --volume /path/to/application.properties:/usr/local/tomcat/config/application.properties eiffel-intelligence-frontend:0.0.19`

2. Put application.properties file in a different folder in container and tell EI where the application.properties is located in the container:

`docker run -p 8070:8080 --expose 8080 --volume /path/to/application.properties:/tmp/application.properties -e spring.config.location=/tmp/application.properties eiffel-intelligence-frontend:0.0.19`


# Run Docker image with provided docker-compose file
This docker-compose file includes these components, [docker-compose.yml](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/docker/docker-compose.yml):
- MongoDB
- RabbitMq
- ER (Event Repository)
- Mail server
- Jenkins
- Eiffel REMReM services (Generate and Publish)
- 3 instances of EI backend (using different rule sets)
- EI frontend (Using the local EI front-end Docker image build from previous steps)

NOTE: Only MongoDB, RabbitMQ, ER and EI components are needed to start.
The rest of the components can be commented out if not needed.

### 1 Source environment variables used in docker-compose.yml

For easier configuration, the Docker images to be used and ports for the different
services are set in [env.bash](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/docker/env.bash)
file. Update to whichever ports you want to use, or keep default values. If you have used a different image tag when you built the EI front-end
docker image, then you need to update the [env.bash file](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/docker/env.bash)
with the locally built image.

To run docker-compose commands, the environment variables needs to be set:

    source src/main/docker/env.bash

Two variables need to be set before we can start up all services with docker-compose tool.
Set Docker host IP to the HOST variable. This is done automatically when sourcing [env.bash](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/docker/env.bash).
But it is also possible to do it manually. If on Linux:
`export HOST=$(hostname -I | tr " " "\n"| head -1)`
If on Windows, get Docker Host IP with command: `dockermachine ip`
Set that Docker host IP to HOST environment variable.

Currently we need to provide EI back-end instances list outside of docker-compose.yml file.
This is also done via the [env.bash](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/docker/env.bash)
file.

### 2 Then run following docker-compose command to startup all components:

    docker-compose -f src/main/docker/docker-compose.yml up -d

It will take some minutes until all components has started. When all components has loaded, you should be able to access EI front-end web page with address:
http://\<docker host ip\>:8081/

Curl command can be used to make request via EI front-end bridge to EI back-end REST API, example for getting all subscriptions:


`curl -X GET http://localhost:8081/subscriptions`

It is also possible to access these Rest-Api addresses in web-browser and get result presented in a Json view in web-browser.

Following command can be used to get the logs from the EI front-end container/service:

`docker-compose -f src/main/docker/docker-compose.yml logs ei_frontend`

All service names can be retrieved with following command:

`docker-compose -f src/main/docker/docker-compose.yml config --services`

It is also possible to retrieve the logs by only using "docker logs <container_id or container_name>" command:

`docker logs <container_id or container_name>`
`docker logs <container_id or container_name>`

Container id can be retrieved with docker command:

`docker ps`