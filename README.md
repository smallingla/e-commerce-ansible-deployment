# GridIron E-Commerce Application

Welcome to the GridIron E-Commerce Application! This repository contains a Spring Boot-based e-commerce application that can be deployed locally using Docker and ansible on a local or live server. Follow the instructions below to get started.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Getting Started Locally](#getting-started-locally)
    - [Option A: With Docker](#option-a-with-docker)
    - [Option B: Without Docker](#option-b-without-docker)
3. [Deployment to a Live Server](#deployment-to-a-live-server)
    - [Preparation](#preparation)
    - [Deploying with Ansible](#deploying-with-ansible)
4. [Environment Variables](#environment-variables)
5. [API Documentation](#api-documentation)
6. [Support](#support)

## Prerequisites

Before you begin, ensure you have the following installed on your system:

- Java 17: Required to build and run the Spring Boot application.
- Maven: For building the project.
- Git: To clone the repository.
- Docker (if you choose the Docker-based setup).
- Ansible (for server deployment).

## Getting Started Locally

### Option A: With Docker Installed on Local Machines

1. Clone the repository from `https://gridirontest.com/bootcamp-cohort-3/springboot-lawal_o.git` and navigate into the project directory.
2. Start the application using the `ansible-playbook deploy-local.yml --ask-become-pass` command on the root folder. Input root password of the machine if prompted to enter a password
3. Access the application by navigating to `http://localhost:8080` in your browser. PostgreSQL will be available on port `5555` by default if needed to be accessed locally.

### Option B: Without Docker

If you do not have Docker installed and do not want to download docker, follow these steps:

1. Install Java 17 and ensure it is available in your PATH.
2. Install PostgreSQL and create a database with the necessary credentials.
3. Clone the repository from `https://gridirontest.com/bootcamp-cohort-3/springboot-lawal_o.git` and navigate into the project directory.
4. Set up the database connection details in `application-development.yml` or use environment variables.
5. Build the application using Maven.
6. Run the application with the `development` profile.
7. Access the application by navigating to `http://localhost:8080` in your browser.

## Deployment to a Live Server

### Preparation

Before deploying to a live server, ensure:

- You have a remote server with SSH access.
- The server is accessible from your local machine.
- You have set up the `deploy-live-server.yml` configuration for Ansible in this repository.

### Deploying with Ansible

1. Set up your Ansible inventory by updating the `hosts` file with your server's IP or domain.
2. Configure environment variables in your `deploy-live-server.yml` playbook or use a `.env` file on the server.
3. Run the Ansible playbook to deploy the application, which will build and package the application, transfer necessary files to the remote server, and start the application using Docker Compose.
4. Access your live application by navigating to `http://your-server-ip:8080` in your browser.

## Environment Variables

The application relies on a few key environment variables for configuration:

- `POSTGRES_USERNAME`: The username for the PostgreSQL database.
- `POSTGRES_PASSWORD`: The password for the PostgreSQL database.
- `SPRING_ACTIVE_PROFILE`: Set to `production` for a production environment and `development` for t development environment
- Other variables can be added as needed in the `.env` file or passed directly through Docker Compose or the Ansible playbook.

## API Documentation

To explore the API, you can use the following tools:

- Postman: Visit `https://documenter.getpostman.com/view/8450919/2sAY4rGRQR` 

## Support

If you encounter any issues or have questions, please feel free to open an issue in this repository or contact the maintainers.
