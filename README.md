# Bhai_IAM - Identity and Access Management System

## Overview
Bhai_IAM is a comprehensive Identity and Access Management system built with Spring Boot, providing OIDC-compliant authentication and fine-grained role-based access control for products and resources.

## Features
- **OIDC-compliant Authentication**: JWT-based authentication compatible with OpenID Connect standards
- **Role-Based Access Control**: Granular permissions for different user roles (Admin, Sales Manager, CRM Rep)
- **Product Access Management**: Control which roles can access specific products
- **RESTful API**: Well-structured API endpoints for authentication and product management
- **Keycloak Integration**: Leverages Keycloak for identity management and authentication

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
- **Keycloak**: Open source identity and access management
- **JUnit 5**: Testing framework
- **Maven**: Dependency management

## Setup and Installation

### Prerequisites
- Java 17 or higher
- MongoDB
- Maven
- Keycloak 26.1.3 or compatible version

### Getting Started
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Bhai_IAM.git
   cd Bhai_IAM
   ```

2. Configure MongoDB:
   - Ensure MongoDB is running on localhost:27017 (or update application.properties with your MongoDB URL)

3. Configure Keycloak:
   - Ensure Keycloak is running on http://localhost:9090
   - Create a realm for the application
   - Set up client for the application with appropriate redirect URIs
   - Configure roles: ADMIN, SALES_MANAGER, CRM_REP
   - Create test users with appropriate roles

4. Build and run the application:
   ```bash
   cd oidc-provider
   mvn clean install
   mvn spring-boot:run
   ```

5. The application will be accessible at `http://localhost:8080`

## Authentication Flow

### Production Authentication
The application uses Keycloak as an OIDC provider for authentication:

1. Users are redirected to Keycloak for authentication
2. After successful authentication, Keycloak issues a JWT token
3. The application validates the token using Keycloak's public keys
4. Token claims, including user roles from the `realm_access` claim, are mapped to Spring Security authorities
5. Access control is enforced based on these authorities

### Test Authentication
For testing purposes, the application uses a mock JWT implementation:

1. `JwtTestUtil` creates test tokens with appropriate roles
2. Test security configuration validates these tokens
3. The token structure mimics Keycloak's token format, including the `realm_access` claim

## API Documentation
API documentation is available through SpringDoc OpenAPI UI at `http://localhost:8080/swagger-ui.html` when the application is running.

## Testing
Run the complete test suite with:
```bash
mvn test
```

### Running Specific Tests
To run the Product Access Control Flow Integration Test:
```bash
cd oidc-provider
mvn test -Dtest=ProductAccessControlFlowIntegrationTest
```

This integration test validates the role-based access control mechanism for products:
- Verifies that Admins can access all products
- Confirms Sales Managers can only access limited products (iPhone, iPad)
- Ensures CRM Representatives can only access specific products (iPhone)
- Tests that only Admins can create new products
- Validates that only Admins can manage product role assignments

The test uses JWT tokens to simulate different user roles and verifies access permissions across various API endpoints.

## External Resources
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security OAuth 2.0 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)

## License
This project is licensed under the MIT License - see the LICENSE file for details. 