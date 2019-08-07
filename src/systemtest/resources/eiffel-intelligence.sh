#!/bin/bash

# This script should be executed from the root of the repository.
# Run: src/systemtest/resources/eiffel-intelligence.sh [COMMAND]

STATUS=0
verbose=0
build=false

export CURRENT_DIR=$(pwd)
echo "Executing script: ${0} from directory: " ${CURRENT_DIR}

function do_build {
    echo "Building Eiffel Intelligence front-end war file"
    # Build war file from latest code changes in EI front-end
    call "mvn clean"
    call "mvn package -DskipTests=true"
    echo "Cloning Eiffel Intelligence back-end and building war file"
    # Clone EI back-end and build war file
    if [[ -d "eiffel-intelligence" ]]; then rm -Rf eiffel-intelligence; fi
    call "git clone --depth=50 --branch=master https://github.com/eiffel-community/eiffel-intelligence.git"
    call "cd eiffel-intelligence"
    call "mvn package -DskipTests=true"
    call "cd .."
}

function do_start {
    echo "Starting up Docker environment"

    ## Set variables for Eiffel Intelligence war files.
    export EI_FRONTEND_WAR_FILE=$(ls target/*.war)
    export EI_BACKEND_WAR_FILE=$(cd eiffel-intelligence && ls target/*.war)

    # Sets Docker image names and ports for containers.
    # Also sets host variable
    source src/main/docker/env.bash > /dev/null

    if [[ $build == "true" ]]; then
        # Set up Docker containers and build the images of EI front-end, back-end and Jenkins
        call "docker-compose -f src/main/docker/docker-compose.yml up -d --build"
    else
        # Set up Docker containers with specified Docker images versions
        call "docker-compose -f src/main/docker/docker-compose.yml up -d"
    fi

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
    source src/main/docker/env.bash > /dev/null
    verbose " Executing: mvn verify -P systemTest -Dei.frontend.url=http://${HOST}:8081 -Djenkins.external.url=http://${HOST}:8082 -B"
    mvn verify -P systemTest -Dei.frontend.url="http://${HOST}:8081" -Djenkins.external.url="http://${HOST}:8082" -B
    STATUS=$?
}

function do_stop {
    echo "Stopping Docker containers ..."
    # Sourcing environment variables to avoid warnings
    source src/main/docker/env.bash > /dev/null
    call "docker-compose -f src/main/docker/docker-compose.yml down"

    # Cleaning up dangling docker images which were built using docker-compose command
    verbose "Removing unused Docker images if there are any"
    images=$(docker images -f "dangling=true" -q)
    if [[ ! -z "$images" ]]; then call "docker rmi $images"; fi
}

function verbose {
  if [[ "$verbose" -eq "1" ]]; then
    echo -e $1
  fi
}

function call {
  verbose "Executing: '$@'"
  $1
}

function usage {
    cat <<EOF

Usage: systemtest.sh COMMAND [OPTIONS]

COMMANDS:
    start :
        Starts up a full Dockerized environment with Eiffel Intelligence and
        surrounding components. The containers are defined in
        src/main/docker/docker-compose.yml. Then checks if containers are up.
    stop :
        Stops the Docker containers defined in src/main/docker/docker-compose.yml.
        Removing dangling docker images after containers are shut down.
    test :
        First sets up full Dockerized environment for Eiffel Intelligence. Then
        check Jenkins container is healthy before starting the system tests
        defined for Eiffel Intelligence, using maven verify command. After
        executing the tests, the Docker containers are stopped and removed.
    check :
        Checks if the Jenkins Docker container is up and running. This container
        is (usually) the last one to start up.
    build :
        Builds a war file on the latest code changes in Eiffel Intelligence front-end.
        It also clones Eiffel Intelligence back-end repository and builds a war file
        based on those code changes.
    only_test :
        Assuming the environment is already set up, this only executes the
        system tests with maven command.

OPTIONS:
    -v | --verbose  : Turn on verbose mode while executing. This echoes the shell
                      commands before executing them.
    -h | --help     : Shows this helps text.

EOF
}


commands=()

while [[ "$1" != "" ]]; do
    case "$1" in
        -v | --verbose)
            verbose=1
            verbose "Running with verbose mode."
            ;;
        -h | --help)
            usage
            exit
            ;;
        *)
            # Save for positional arguments
            commands+=("$1")
    esac
    shift
done

# Restore positional arguments to be executed
set -- "${commands[@]}"

if [[ -z "${commands}" ]]; then usage exit 1; fi

while [[ "$@" ]]; do
    case "$1" in
            build)
                do_build
                build="true"
                ;;
            start)
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
                usage
                exit 1
    esac
    shift
done

exit ${STATUS}