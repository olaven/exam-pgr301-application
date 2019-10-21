#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker build . --tag geiger
docker tag geiger "${DOCKER_USERNAME}/geiger:latest"
docker push "${DOCKER_USERNAME}/geiger"