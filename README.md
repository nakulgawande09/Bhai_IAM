# Bhai_IAM - Identity and Access Management System

## Overview
Bhai_IAM is a comprehensive Identity and Access Management system built with Spring Boot, providing OIDC-compliant authentication and fine-grained role-based access control for products and resources.

## Features
- **OIDC-compliant Authentication**: JWT-based authentication compatible with OpenID Connect standards
- **Role-Based Access Control**: Granular permissions for different user roles (Admin, Sales Manager, CRM Rep)
- **Product Access Management**: Control which roles can access specific products
- **RESTful API**: Well-structured API endpoints for authentication and product management

## Project Structure
- **oidc-provider/**: Core authentication and product management implementation
  - **controllers/**: REST API endpoints
  - **models/**: Data models (User, Product)
  - **repositories/**: Data access layer
  - **services/**: Business logic
  - **exceptions/**: Custom exception handling
  - **config/**: Security and application configuration

## Technologies Used
- **Spring Boot 3.1.5**: Core framework
- **Spring Security**: Authentication and authorization
- **Spring Data MongoDB**: Data persistence
- **OAuth 2.0 & OIDC**: Token-based authentication
- **JUnit 5**: Testing framework
- **Maven**: Dependency management

## Setup and Installation

### Prerequisites
- Java 17 or higher
- MongoDB
- Maven

### Getting Started
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Bhai_IAM.git
   cd Bhai_IAM
   ```

2. Configure MongoDB:
   - Ensure MongoDB is running on localhost:27017 (or update application.properties with your MongoDB URL)

3. Build and run the application:
   ```bash
   cd oidc-provider
   mvn clean install
   mvn spring-boot:run
   ```

4. The application will be accessible at `http://localhost:8080`

## API Documentation
API documentation is available through SpringDoc OpenAPI UI at `http://localhost:8080/swagger-ui.html` when the application is running.

## Testing
Run the test suite with:
```bash
mvn test
```

## License
This project is licensed under the MIT License - see the LICENSE file for details. 