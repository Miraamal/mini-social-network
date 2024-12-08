# Mini Social Network API

## Overview
This project is a backend RESTful API for a mini social network that allows users to create posts, like and comment on posts, and interact with other users. The application is built using Spring Boot and Java, with security provided through JWT authentication.

### Features:
- User authentication and registration
- CRUD operations for posts
- Commenting on posts
- Liking posts
- User profile management
- Admin management capabilities

## Technologies Used
- **Java 17**: Main programming language.
- **Spring Boot**: Backend framework for building the API.
- **Spring Security**: Used for authentication and authorization.
- **JWT**: For stateless user authentication.
- **PostgreSQL**: Database for data persistence.
- **Lombok**: To reduce boilerplate code.
- **Swagger (OpenAPI)**: For API documentation and testing.

## Getting Started

### Prerequisites
- **Java 17**
- **Gradle**: For building and running the application.
- **PostgreSQL**: A running instance for database access.

### Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/your-username/mini-social-network.git
   cd mini-social-network
   ```
2. Update the `application.yml` file with your PostgreSQL database credentials:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/your_database
       username: your_db_username
       password: your_db_password
   ```

3. Build the application using Gradle:
   ```sh
   ./gradlew build
   ```

4. Run the application:
   ```sh
   ./gradlew bootRun
   ```

### Running with Docker
You can also use Docker to run the application:
1. Build and start the Docker containers:
   ```sh
   docker-compose up -d
   ```
   Make sure to adjust the database configuration if needed in the `docker-compose.yml` file.

## API Endpoints

### Authentication
- **POST** `/api/auth/register`: Register a new user.
- **POST** `/api/auth/login`: Authenticate and generate a JWT.

### User Management
- **PUT** `/api/user/{userId}`: Update user profile. (Requires authentication)
- **DELETE** `/api/user/{userId}`: Delete user profile. (Users can only delete their own profiles)
- **GET** `/api/user/{userId}`: Get user details by ID.
- **GET** `/api/user`: Get a list of all users. (Admin only)

### Post Management
- **POST** `/api/posts`: Create a new post. (Requires authentication)
- **GET** `/api/posts`: Retrieve all posts. (Admin only)
- **GET** `/api/posts/user/{userId}`: Retrieve all posts by a specific user. (Requires user or admin)
- **GET** `/api/posts/user/{userId}/filtered`: Retrieve filtered posts by user (by time or popularity).
- **PUT** `/api/posts/{postId}`: Update a post. (Only the owner or admin can update)
- **DELETE** `/api/posts/{postId}`: Delete a post. (Only the owner or admin can delete)

### Comment Management
- **POST** `/api/posts/{postId}/comments`: Add a comment to a post. (Requires authentication)

## Security
This application uses Spring Security and JWT for authentication and authorization. Only authenticated users can perform certain actions, and admin roles have additional privileges such as managing all users and posts.

### Roles:
- **USER**: Can register, create posts, update and delete their own posts/comments, and like/comment on posts.
- **ADMIN**: Can perform all operations, including managing other users' posts and accounts.

## OpenAPI Documentation
Swagger UI is available for easy testing and visualization of the API. After starting the application, visit:
```
http://localhost:8080/swagger-ui.html
```

