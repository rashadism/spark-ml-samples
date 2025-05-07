#!/bin/bash

./gradlew bootRun &
APP_PID=$!

cleanup() {
  echo "Terminating Spring Boot server..."
  kill $APP_PID
}
trap cleanup EXIT

echo "Waiting for the server to spin up..."
until curl -s http://localhost:9090/health | grep -q "UP"; do
  sleep 1
done

echo "Opening http://localhost:9090 in the browser..."
xdg-open http://localhost:9090

wait $APP_PID
