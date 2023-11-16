#!/usr/bin/env bash

COMPOSE_FILE="docker-compose.yaml"
COMPOSE_FILE_CERT="cert-docker-compose.yaml"
# Certificate files and directories
CERTBOT_ETC="certbot-etc"
REQUIRED_DIRECTORIES=("accounts" "archive" "live" "renewal" "renewal-hooks")
# UPDATE DIRECTORY HERE!
CERT_FILE1="certbot-etc/live/x/fullchain.pem"
CERT_FILE2="certbot-etc/live/x/privkey.pem"

# Stop running containers
docker-compose -f $COMPOSE_FILE_CERT down --remove-orphans
docker-compose -f $COMPOSE_FILE down --remove-orphans

# Check if certbot-etc directory exists
if [ ! -d "$CERTBOT_ETC" ]; then
    # If not, create certbot-etc
    mkdir -p "$CERTBOT_ETC"
    echo "Directory created: $CERTBOT_ETC"
else
    echo "Directory already exists: $CERTBOT_ETC"
fi

# Check and create required subdirectories inside certbot-etc
for dir in "${REQUIRED_DIRECTORIES[@]}"; do
    subdirectory="$CERTBOT_ETC/$dir"

    if [ ! -d "$subdirectory" ]; then
        mkdir -p "$subdirectory"
        echo "Subdirectory created: $subdirectory"
    else
        echo "Subdirectory already exists: $subdirectory"
    fi
done

# Run docker-compose-cert.yaml
docker-compose -f $COMPOSE_FILE_CERT up -d
echo "Checking certificate."

# Wait for the files to appear with a timeout of 1 minute
TIMEOUT=$((SECONDS + 60))
counter=0
while [ ! -f "$CERT_FILE1" ] || [ ! -f "$CERT_FILE2" ]; do
    counter=$((counter + 1))
    echo "Getting certificate... $counter"
    sleep 1
    if [ "$SECONDS" -gt "$TIMEOUT" ]; then
        echo "Error: Certificate files did not appear within 1 minute. Exiting."
        exit 1
    fi
done

# Wait for certbot to finish and exit
CERTBOT_CONTAINER_ID=$(docker-compose -f $COMPOSE_FILE_CERT ps -q certbot)
docker wait "$CERTBOT_CONTAINER_ID"

# Stop the containers started by cert-docker-compose.yaml
docker-compose -f $COMPOSE_FILE_CERT down --remove-orphans
echo "Certificate checked."

# Run up the containers
docker-compose -f $COMPOSE_FILE up -d
echo "Application started."