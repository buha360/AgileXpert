# Device Manager

## Overview

Device Manager is a full-stack application that simulates a customizable desktop-like environment for group-based users.

The project consists of a Spring Boot backend and an Angular frontend. Users can create and enter groups, log in with their own accounts, manage wallpapers and themes, organize applications into folders, and launch applications from a personal menu interface.

The project was built as a combined backend and frontend solution, with API communication based on an OpenAPI contract. The backend provides the REST API and persistence layer, while the frontend provides the interactive desktop-style user experience.

## Project Structure

    DeviceManager/
    ├── Backend/
    │   └── README.md
    ├── Frontend/
    │   └── README.md
    └── README.md

## Main Idea

The application models a user-specific smart menu system with the following concepts:

- Groups with access codes
- User accounts inside groups
- Admin and normal user roles
- Personal root menu
- Favorite applications
- Folders / submenus
- Wallpapers
- Themes
- Application launch actions

The frontend presents this system in a desktop-inspired UI, while the backend manages data, business rules, and API endpoints.

## Backend

The backend is implemented with Spring Boot and is responsible for:

- Group management
- User management
- Authentication logic
- Menu and submenu handling
- Wallpaper and theme management
- Application launch endpoints
- Database persistence
- Liquibase changelogs
- OpenAPI specification and backend-side API generation

For detailed backend setup, architecture, profiles, and database information, see:

`Backend/README.md`

## Frontend

The frontend is implemented with Angular and is responsible for:

- Group entry flow
- Group login flow
- Admin interface
- Desktop-style user menu
- Folder navigation
- Drag and drop interactions
- Custom confirmation and launch modals
- Wallpaper and theme visualization
- Generated OpenAPI Angular client usage

For detailed frontend setup, generation steps, development server usage, and UI structure, see:

`Frontend/README.md`

## Technologies Used

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Liquibase
- H2 / PostgreSQL / Oracle
- Maven
- OpenAPI Generator

### Frontend

- Angular
- TypeScript
- HTML
- CSS
- Angular Router
- Angular HttpClient
- OpenAPI Generator

## API Contract

The project uses an OpenAPI-based API contract. The backend specification is used to generate:

- Backend API interfaces and models
- Frontend Angular API client code

## Main Features

- Create groups with an admin account
- Enter groups using an access code
- User login inside a selected group
- Admin page for managing users
- User desktop with wallpapers and themes
- Favorite applications
- Folder creation and folder navigation
- Add and remove applications
- Prevent duplicate application placement
- Launch applications with popup feedback
- Drag and drop deletion for applications and folders

## Running the Project

In general, the project is started in two parts:

1. Start the backend from the `Backend` folder.
2. Start the frontend from the `Frontend` folder.
3. Open the frontend in the browser.

The backend and frontend have their own dedicated README files with the exact commands and setup details.

This top-level README only provides a high-level overview of the project.

For detailed information, use the dedicated documentation inside each part of the project:

- `Backend/README.md`
- `Frontend/README.md`