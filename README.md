Secure Login System

A secure login system built with **Spring Boot**, **Spring Security**, and **JWT** authentication. This project provides user registration, login, and role-based access control, with API documentation generated using **Swagger UI** (springdoc-openapi).

 Features
- User registration with role assignment (e.g., `ROLE_USER`, `ROLE_ADMIN`).
- User login with JWT token generation.
- Role-based access control for protected endpoints.
- API documentation via Swagger UI.
- Secure password storage using BCrypt.
- Stateless authentication using JWT.

## Technologies
- **Java 17**
- **Spring Boot 3.5.0**
- **Spring Security 6.5.0**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (jjwt 0.12.6)**
- **springdoc-openapi 2.8.8**
- **Lombok**
- **Maven**

 Prerequisites
- **Java 17** installed (e.g., OpenJDK or Oracle JDK).
- **Maven** installed (version 3.8+ recommended).
- **PostgreSQL** installed and running.
- **Postman** or a similar tool for API testing (optional).

 Setup and Installation
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/secure-login-system.git
   cd secure-login-system
   ```

2. **Configure PostgreSQL**:
   - Create a database named `secure_login_db`:
     ```sql
     CREATE DATABASE secure_login_db;
     ```
   - Update the database credentials in `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/secure_login_db
     spring.datasource.username=postgres
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.show-sql=true
     ```

3. **Configure JWT Secret**:
   - In `application.properties`, set a secure JWT secret key (at least 32 characters):
     ```properties
     jwt.secret=your-secure-secret-key-1234567890
     jwt.expiration=86400000
     ```

4. **Configure Swagger UI**:
   - Ensure the following properties are set in `application.properties`:
     ```properties
     springdoc.api-docs.path=/v3/api-docs
     springdoc.swagger-ui.path=/swagger-ui.html
     springdoc.swagger-ui.enabled=true
     springdoc.packages-to-scan=com.mvp.op.controller
     springdoc.swagger-ui.csrf.enabled=false
     springdoc.webjars.prefix=/webjars
     ```

5. **Install Dependencies**:
   - Run the following command to download dependencies:
     ```bash
     mvn clean install -DskipTests
     ```

## Running the Application
1. **Start the Application**:
   ```bash
   mvn spring-boot:run
   ```
   - The application will run on `http://localhost:8080`.

2. **Access the API Documentation**:
   - Open `http://localhost:8080/swagger-ui.html` or `http://localhost:8080/swagger-ui/index.html` in your browser to view the Swagger UI.
   - If you encounter a 403 error, refer to the [Troubleshooting](#troubleshooting) section.

## API Documentation
The API is documented using **Swagger UI**. Key endpoints include:
- **POST /api/auth/register**: Register a new user.
  ```json
  {
    "username": "testuser",
    "password": "testpass",
    "role": "USER"
  }
  ```
- **POST /api/auth/login**: Authenticate a user and receive a JWT token.
  ```json
  {
    "username": "testuser",
    "password": "testpass"
  }
  ```
- **GET /api/test/admin**: Access a protected endpoint (requires `ROLE_ADMIN`).

To authorize requests in Swagger UI:
1. Click the **Authorize** button (padlock icon).
2. Enter `Bearer <token>` (replace `<token>` with the JWT from `/api/auth/login`).
3. Test protected endpoints.

## Testing the API
1. **Using Postman**:
   - **Register**: Send a `POST` to `http://localhost:8080/api/auth/register` with the JSON body above.
   - **Login**: Send a `POST` to `http://localhost:8080/api/auth/login` and copy the JWT token.
   - **Test Admin Endpoint**: Send a `GET` to `http://localhost:8080/api/test/admin` with the header:
     ```
     Authorization: Bearer <token>
     ```

2. **Using Swagger UI**:
   - Access `http://localhost:8080/swagger-ui.html`.
   - Use the **Authorize** button to set the JWT token.
   - Test endpoints directly in the browser.

## Project Structure
```
secure-login-system/
├── src/
│   ├── main/
│   │   ├── java/com/mvp/op/
│   │   │   ├── controller/       # REST controllers (e.g., AuthController)
│   │   │   ├── security/         # Security configuration and JWT filter
│   │   │   ├── exception/        # Global exception handling
│   │   │   ├── model/            # Entities (e.g., User)
│   │   │   ├── repository/       # JPA repositories
│   │   │   ├── service/          # Business logic
│   │   ├── resources/
│   │       ├── application.properties  # Configuration (database, JWT, Swagger)
│   ├── test/                     # Unit and integration tests
├── pom.xml                       # Maven dependencies
├── README.md                     # Project documentation
```

## Troubleshooting
- **HTTP 403 on Swagger UI (`/swagger-ui/index.html`)**:
  - Verify that the following paths are permitted in `SecurityConfig.java`:
    ```java
    .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html", "/webjars/**", "/swagger-resources/**", "/configuration/**").permitAll()
    ```
  - Ensure `JwtAuthenticationFilter.java` skips these paths:
    ```java
    if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.startsWith("/webjars") || path.startsWith("/swagger-resources") || path.startsWith("/configuration"))
    ```
  - Enable debug logs in `application.properties`:
    ```properties
    logging.level.org.springframework.security=DEBUG
    logging.level.org.springdoc=DEBUG
    logging.level.org.springframework.web=DEBUG
    ```
  - Check the browser's Network tab (F12 > Network) for failed requests (e.g., `/webjars/swagger-ui/*`).
  - Test `/v3/api-docs` in Postman to confirm it returns 200 OK.
  - Clear Maven cache:
    ```bash
    mvn clean install -DskipTests
    ```

- **Database Connection Issues**:
  - Ensure PostgreSQL is running and credentials are correct in `application.properties`.
  - Verify the database URL: `jdbc:postgresql://localhost:5432/secure_login_db`.

- **JWT Errors**:
  - Ensure `jwt.secret` is a secure, 32+ character string in `application.properties`.
  - Check token expiration (`jwt.expiration=86400000` for 24 hours).

- **Swagger UI Not Displaying Endpoints**:
  - Confirm `springdoc.packages-to-scan=com.mvp.op.controller` matches your controller package.
