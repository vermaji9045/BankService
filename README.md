Microservices System

This repository contains multiple microservices that form part of a larger distributed system. The system uses technologies such as Kubernetes, Docker, Spring Boot, and Eureka for service discovery, load balancing, and orchestration.

Microservices

1. Accounts Service - Manages user accounts and related information.
2. Loans Service - Handles loan-related operations and data.
3. Card Service - Manages card-related operations.
4. Config Server - Centralized configuration server for externalizing service configurations.
5. Eureka Server - Service registry for microservices.
6. Gateway Server - Acts as an entry point for client requests and routes them to the appropriate services.
7. Message Service - Handles messaging functionality within the system.
8. Kubernetes Setup - Contains Kubernetes manifests for deploying services.
9. Docker Compose Setup - Docker Compose file for local orchestration and testing.

Prerequisites

- Java 17: Required to build and run Spring Boot applications.
- Maven: For managing dependencies and building the project.
- Docker: For containerizing services.
- Kubernetes: To deploy and manage the microservices in a cluster.
- kubectl: CLI tool for interacting with Kubernetes.
- Minikube (optional): For running Kubernetes locally.

Setup and Installation

1. Clone the repository:
    git clone https://github.com/yourusername/yourrepository.git
    cd yourrepository

2. Build the microservices:
    mvn clean install

3. Dockerize the services:
    Each microservice has its own Dockerfile. To build Docker images, run:
    docker-compose build

Kubernetes Deployment

To deploy the services on a Kubernetes cluster, ensure kubectl is configured to point to the correct cluster and then apply the manifests:

kubectl apply -f kubernetes/

This will deploy all microservices, along with the Config Server, Eureka Server, and Gateway Server, into the Kubernetes cluster.

Docker Compose

For local development and testing, you can use Docker Compose to spin up all the microservices and supporting infrastructure:

docker-compose up

This will start all services defined in the docker-compose.yml file, including:
- Accounts Service
- Loans Service
- Card Service
- Eureka Server
- Gateway Server
- Config Server
- Message Service

Service Architecture

The architecture follows the microservices design pattern, where each service is independent and communicates via REST APIs. Service discovery is managed by Eureka, and client requests are routed through the Gateway Server.

High-Level Components:
1. Config Server: Provides externalized configuration to all services.
2. Eureka Server: Handles service discovery, allowing microservices to locate each other dynamically.
3. Gateway Server: Acts as the API gateway, forwarding client requests to the appropriate microservices.
4. Business Services: Consists of Accounts, Loans, and Card services that manage core business logic.
5. Message Service: Manages internal messaging between services.

License

This project is licensed under the MIT License - see the LICENSE file for details.
