# OIDC Provider

A Spring Boot application implementing an OAuth2 Resource Server with MongoDB for data persistence.

## Features

- Spring Boot 3.1.5 with Java 17
- OAuth2 Resource Server for securing APIs
- MongoDB for data persistence
- Swagger/OpenAPI for API documentation
- Spring Security for authentication and authorization
- Keycloak integration for OAuth2/OIDC authentication

## Project Structure

```
com.auth.oidcprovider
├── config          - Security and MongoDB configurations
├── controllers     - REST API controllers
├── models          - Data models/entities
├── repositories    - Data access interfaces
└── services        - Business logic implementations
```

## Domain Model

The application uses a structured domain model with MongoDB:

- **BaseEntity**: Abstract base class with common fields:
  - id: MongoDB document ID
  - createdAt: Timestamp when the entity was created (audited)
  - updatedAt: Timestamp when the entity was last updated (audited)

- **User**: Extends BaseEntity with:
  - username: Unique username
  - email: Unique email address
  - keycloakId: ID linked to Keycloak user
  - roles: List of user roles
  - active: User status flag

## Repositories

The application uses Spring Data MongoDB repositories:

- **UserRepository**: Provides CRUD operations for User entities with:
  - findByUsername: Find user by username
  - findByEmail: Find user by email
  - findByKeycloakId: Find user by Keycloak ID
  - findByRole: Find users with a specific role
  - findAllActiveUsers: Find all active users

## Requirements

- Java 17 or higher
- MongoDB
- Maven
- Keycloak server (for authentication)

## MongoDB Configuration

The application is configured to connect to MongoDB at:

- Host: localhost
- Port: 27017
- Database: IAM

MongoDB connection is configured in `application.yml` and can be customized by updating the following properties:

```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: IAM
```

Make sure MongoDB is running on your local machine or update the configuration to point to your MongoDB server.

## Security Configuration

The application is configured as an OAuth2 Resource Server that integrates with Keycloak for authentication and authorization.

### Security Features

- JWT token validation using Keycloak's public keys
- Role-based authorization (ROLE_ADMIN, ROLE_USER)
- CORS configuration
- CSRF protection
- Stateless session management

### Endpoint Security

- Public endpoints: `/api/public/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- Admin-only endpoints: `/api/admin/**`
- User endpoints (requires USER or ADMIN role): `/api/users/**`
- All other endpoints require authentication

### Keycloak Configuration

Update the following properties in `application.yml` to point to your Keycloak server:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/iam-realm
          jwk-set-uri: http://localhost:8080/realms/iam-realm/protocol/openid-connect/certs

keycloak:
  realm: iam-realm
  auth-server-url: http://localhost:8080/auth
  resource: iam-client
  public-key: YOUR-PUBLIC-KEY
  bearer-only: true
```

### Setting Up Keycloak

1. Install and start Keycloak server
2. Create a new realm named `iam-realm`
3. Create a new client named `iam-client` with the following settings:
   - Access Type: bearer-only
   - Standard Flow Enabled: OFF
   - Direct Access Grants Enabled: ON
   - Service Accounts Enabled: ON
4. Create roles: ADMIN, USER
5. Create users and assign roles

## Building the Application

To build the application:

```bash
mvn clean install
```

## Running the Application

To run the application:

```bash
mvn spring-boot:run
```

The application will be available at http://localhost:8081.

## API Documentation

Swagger UI: http://localhost:8081/swagger-ui.html
OpenAPI JSON: http://localhost:8081/v3/api-docs 