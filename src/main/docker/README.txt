A: Build Eiffel-Intelligence Front-end Docker image based on Eiffel-Intelligence Front-end from an Artifactory, e.g. Jitpack:
cd (git root dir)
docker build -t eiffel-intelligence-frontend:0.0.19 --build-arg URL=https://jitpack.io/com/github/eiffel-community/eiffel-intelligence-frontend/0.0.19/eiffel-intelligence-frontend-0.0.19.war -f src/main/docker/Dockerfile .


B: Build Eiffel-Intelligence Front-end based on local Eiffel-Intelligence Front-end source code changes
1. Build Eiffel-Intelligence Front-end artifact:
cd (git root dir)
mvn package -DskipTests

2. Build Eiffel-Intelligence Front-end Docker image:
cd (git root dir)/
docker build -t eiffel-intelligence-frontend:0.0.19 --build-arg URL=./target/eiffel-intelligence-frontend-0.0.19.war -f src/main/docker/Dockerfile .
