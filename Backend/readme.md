# Device Manager Backend

A Spring Boot based backend application for an intelligent device management system.  
The system supports groups, users, menus, submenu handling, application launching, wallpaper selection, theme selection, REST API documentation, CLI interaction, and OpenAPI-based contract generation.

## Features

The backend currently includes:

- group registration and access code validation
- user creation, authentication and deletion
- root menu handling
- submenu creation, listing and deletion
- adding and removing applications from menus
- application launching
- wallpaper selection
- theme selection
- interactive CLI-based usage
- REST API endpoints
- Swagger UI / OpenAPI documentation
- OpenAPI specification file and generator setup
- controller tests
- JPA repository tests

## Technologies Used

- **Java 21**
- **Spring Boot 4**
- **Spring Web**
- **Spring Data JPA**
- **Hibernate**
- **Liquibase**
- **H2**
- **PostgreSQL**
- **Oracle Database**
- **Maven**
- **Lombok**
- **MapStruct**
- **Springdoc OpenAPI / Swagger UI**
- **OpenAPI Generator**
- **JUnit 5**
- **Mockito**
- **Docker / Docker Compose**

## Project Structure

```text
src
 ├─ main
 │   ├─ java
 │   │   └─ hu.wardanger.devicemanager
 │   │       ├─ cli
 │   │       ├─ config
 │   │       ├─ controller
 │   │       ├─ entity
 │   │       ├─ mapper
 │   │       ├─ models
 │   │       ├─ repository
 │   │       └─ service
 │   └─ resources
 │       ├─ application.yml
 │       ├─ application-h2.yml
 │       ├─ application-postgres.yml
 │       ├─ application-oracle.yml
 │       └─ openapi
 │           └─ device-manager-openapi.yaml
 └─ test
     └─ java
```

## Database Profiles

The project supports three database profiles:

- `h2`
- `postgres`
- `oracle`

Shared configuration is stored in:

`src/main/resources/application.yml`

Profile-specific datasource settings are stored in:

- `application-h2.yml`
- `application-postgres.yml`
- `application-oracle.yml`

## Running the Application

### Run with H2

`mvn spring-boot:run`
or
`mvn spring-boot:run -Ph2`

### Run with PostgreSQL

`mvn spring-boot:run -Ppostgres`

### Run with Oracle

`mvn spring-boot:run -Poracle`

## Docker Compose

A PostgreSQL and Oracle database can be started with Docker Compose.

### Start OracleDB

`docker compose up -d`

### Start PostgreSQL

`docker compose -f docker-compose-postgres.yml up -d`

## Swagger / OpenAPI

After starting the backend, Swagger UI is available at:

`http://localhost:8080/swagger-ui/index.html`

The generated OpenAPI JSON is available at:

`http://localhost:8080/v3/api-docs`

The handwritten OpenAPI specification file is located at:

`src/main/resources/openapi/device-manager-openapi.yaml`

## OpenAPI Code Generation

The project contains OpenAPI Generator Maven Plugin configuration. Based on the OpenAPI specification, API interfaces and request/response models can be generated.

### Generate sources

`mvn generate-sources`

Generated files are placed under:

`target/generated-sources/openapi`

## Testing

### Run all tests

`mvn test`

### Included test types

- controller tests with mocked service layer
- JPA repository tests with `@DataJpaTest`

## CLI

The application also contains an interactive command-line interface.

The CLI supports:

- group listing
- group creation
- entering a group
- user-level menu operations
- submenu operations
- wallpaper and theme selection

When the backend starts, the CLI runner is also started from the Spring Boot application.

## Seeded Data

Liquibase inserts default seed data into the database, including:

- default group
- default admin user
- default root menu
- default wallpaper
- default theme
- default applications

## Example Applications

Example applications currently used in the system:

- Minesweeper
- OpenMap
- Paint
- Contacts

## Notes

- Liquibase is used for schema creation and seed data management.
- Hibernate is used as the JPA provider.
- The REST layer is documented with OpenAPI annotations.
- The project also includes an OpenAPI specification for contract-oriented generation support.
- PostgreSQL and Oracle profiles can be used for external database testing.
