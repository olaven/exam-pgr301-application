#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker build . --tag geiger
docker tag geiger lagasild/geiger:latest
docker push lagasild/geiger