#!/bin/bash

# Start Spring Boot backend
(cd backend && mvn spring-boot:run) &

# Give backend a few seconds to start
sleep 5

# Start React frontend
(cd frontend && npm run dev)