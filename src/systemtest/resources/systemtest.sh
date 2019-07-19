#!/usr/bin/env bash

# This script should be executed from the root of the repository.
# Run: src/systemtest/resources/systemtest.sh [COMMAND]

STATUS=0

export CURRENT_DIR=$(pwd)
echo "Executing script: ${0} from directory: " ${CURRENT_DIR}

function do_build {
    echo "Building Eiffel Intelligence front-end war file"
    # Build war file from latest code changes in EI front-end
    mvn clean -q
    mvn package -DskipTests=true -q

    # Clone EI back-end and build war file
    echo "Cloning Eiffel Intelligence back-end and building war file"
    if [[ -d "eiffel-intelligence" ]]; then rm -Rf eiffel-intelligence; fi
    git clone -q --depth=50 --branch=master https://github.com/eiffel-community/eiffel-intelligence.git
    cd eiffel-intelligence
    mvn package -DskipTests=true -q
    cd ..
}

function do_start {
    echo "Starting up Docker environment"

    ## Set variables for Eiffel Intelligence war files.
    export EI_FRONTEND_WAR_FILE=$(ls target/*.war)
    export EI_BACKEND_WAR_FILE=$(cd eiffel-intelligence && ls target/*.war)

    # Sets Docker image names and ports for containers.
    # Also sets host variable
    source src/main/docker/env.bash

    # Set up docker containers and build the images of EI front-end, back-end and Jenkins
    docker-compose -f src/main/docker/docker-compose.yml up -d --build

    echo "Sleeping for 2 minutes, to let containers start up properly"
    sleep 2m
}

function do_check {
    echo "Checking if Jenkins container is up"

    HTTP_CODE=503
    until [[ $HTTP_CODE -eq "403" ]]; do
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}\n" localhost:8082)
        printf "."
        sleep 5
    done
    printf " Jenkins container is up and running!\n"
}

function do_test {
    echo "Starting system test"
    # Set host variable
    source src/main/docker/env.bash
    mvn verify -P systemTest -Dei.frontend.url="http://${HOST}:8081" -Djenkins.external.url="http://${HOST}:8082" -B
    STATUS=$?
}

function do_stop {
    echo "Stopping Docker containers ..."
    # Sourcing environment variables to avoid warnings
    source src/main/docker/env.bash
    docker-compose -f src/main/docker/docker-compose.yml down

    # Cleaning up dangling docker images which were built using docker-compose command
    # docker rmi $(docker images -f "dangling=true" -q)
}

case "$1" in
        build)
            do_build
            ;;
        start)
            do_build
            do_start
            do_check
            ;;
        check)
            do_check
            ;;
        stop)
            do_stop
            ;;
        test)
            do_build
            do_start
            do_check
            do_test
            do_stop
            ;;
        only_test)
            do_test
            ;;
        *)
            echo $"Usage: $0 [ start|stop|test|check|build|only_test ]"
            exit 1
esac

exit ${STATUS}