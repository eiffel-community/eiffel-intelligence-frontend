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
change the docker images used for the Eiffel Intelligence services.

To set up the environment for running system tests (from root directory):

    source src/main/docker/env.bash
    docker-compose -f src/main/docker/docker-compose.yml up -d


Or start up specific services (only ones needed for integration tests) by listing them like so:

    docker-compose -f src/main/docker/docker-compose.yml up -d eiffel-er mongodb rabbitmq jenkins mail-server

NOTE: Integration tests does not need a frontend started in a docker container,
since Spring will start up EI instances for each integration test.
