# Device Manager Frontend

## Overview

This frontend application provides the user interface for the Device Manager project. It was implemented with Angular and communicates with the backend through a generated OpenAPI client. The application simulates a desktop-like environment where users can enter a group, log in with their account, manage wallpapers and themes, organize applications into folders, and launch applications from the main menu or from submenus.

The backend exposes the REST API and the frontend consumes it through code generated from the OpenAPI specification.

---

## Main Features

- Create and enter groups
- Validate group access with access code
- Select a user account inside a group
- Log in as admin or member
- Admin page for managing group users
- Desktop-like main menu UI
- Favorite applications section
- Folder creation and navigation
- Add applications to the root menu
- Add applications to submenus
- Prevent duplicate application placement across root menu and folders
- Drag and drop removal for applications
- Drag and drop deletion for folders
- Wallpaper and theme selection
- Custom modal dialogs for confirmations and launch messages
- Launch application popup feedback
- Consistent dark glassmorphism-inspired design

---

## Technologies Used

- Angular
- TypeScript
- HTML
- CSS
- Angular Router
- Angular HttpClient
- OpenAPI Generator
- Generated TypeScript Angular API client

---

## Project Structure

```text
Frontend/
├── src/
│   ├── app/
│   │   ├── generated-api/
│   │   ├── groups-page/
│   │   ├── group-login-page/
│   │   ├── group-admin-page/
│   │   ├── user-menu-page/
│   │   ├── submenu-page/
│   │   ├── app.config.ts
│   │   ├── app.routes.ts
│   │   └── ...
│   ├── assets/
│   │   └── wallpapers/
│   ├── styles.css
│   └── main.ts
├── proxyconfig/
│   └── dev.proxy.config.json
├── angular.json
├── package.json
└── README.md
```

## OpenAPI Client Generation

The frontend uses a generated Angular client based on the backend OpenAPI specification. This allows the frontend and backend to follow the same API contract.

`npx @openapitools/openapi-generator-cli generate -i ../Backend/src/main/resources/openapi/device-manager-openapi.yaml -g typescript-angular -o src/app/generated-api`

## User Flow

### Group Entry
The first page allows the user to:

- create a new group
- select an existing group
- enter the group using an access code

### After successful group access validation:

- users belonging to the group are listed
- the user selects an account
- password validation is performed
- admin users are redirected to the admin page
- normal users are redirected to their main menu

## Admin Flow
### Admins can:

- create new users
- view all users in the group
- delete non-admin users
- open their own menu
- Desktop Menu

### The main user menu supports:

- wallpaper selection
- theme selection
- application launching
- folder creation
- application placement into favorites
- folder navigation
- drag and drop deletion for applications and folders

## Folder View
### Inside a folder:

- assigned applications are shown
- new applications can be added
- duplicate placement is prevented across the whole system
- applications can be removed
- launch popup feedback is shown