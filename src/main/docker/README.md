## A: Build Eiffel Intelligence frontend Docker image based on Eiffel Intelligence frontend from an Artifactory, e.g. Jitpack:
cd (git root dir)
docker build -t eiffel-intelligence-frontend --build-arg URL=https://jitpack.io/com/github/eiffel-community/eiffel-intelligence-frontend/1.0.1/eiffel-intelligence-frontend-1.0.1.war -f src/main/docker/Dockerfile .


## B: Build Eiffel Intelligence frontend based on local source code changes
1. Build Eiffel Intelligence frontend artifact:
cd (git root dir)
mvn package -DskipTests

2. Build Eiffel-Intelligence frontend Docker image:
cd (git root dir)/
export EIFFEL_WAR=$(ls target/*.war)
docker build -t eiffel-intelligence-frontend --build-arg URL=./${EIFFEL_WAR} -f src/main/docker/Dockerfile .


## Use docker-compose to set up eco system for testing Eiffel Intelligence

By using the docker-compose file in this directory it is possible to set up a
complete environment to run integration tests and/or system test scenarios
for Eiffel Intelligence. If you want to test with your latest local changes,
add the '--build' flag and docker-compose will build Eiffel Intelligence 
and Jenkins images based on the instructions in docker-compose.yml file.

**To set up environment with latest code changes of Eiffel Intelligence (from root directory):**
The --build flag ensures the Docker images will be rebuilt if you have other 
Docker images with the same name:tag lying around. Eiffel Intelligence back-ends
and front-end and Jenkins containers will be built before starting up the environment. 

    source src/main/docker/env.bash
    docker-compose -f src/main/docker/docker-compose.yml up -d --build

**To run with older (specific) versions of Eiffel Intelligence pulled from DockerHub:**
To avoid building any Eiffel Intelligence images that doesn't exist locally,
run the command 'docker-compose pull serviceName' on the images you want.
This will pull the image with the version specified in the docker-compose.yml
from DockerHub, so that when up command is run this image will be used, and 
it will not be rebuilt.

    source src/main/docker/env.bash
    docker-compose -f src/main/docker/docker-compose.uml pull <serviceName>
    docker-compose -f src/main/docker/docker-compose.yml up -d

**Start up specific services (only ones needed for integration tests) by listing them like so:**

    docker-compose -f src/main/docker/docker-compose.yml up -d eiffel-er mongodb rabbitmq jenkins mail-server

NOTE: Integration tests does not need a frontend started in a docker container,
since Spring will start up EI instances for each integration test.
