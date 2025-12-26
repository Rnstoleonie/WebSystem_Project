# Grade Portal Backend - Spring Boot REST API

A comprehensive Spring Boot backend for a Grade Portal system with role-based access control for Admin, Teacher, and Student roles.

## Features

- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **Admin Dashboard**: Manage teachers, handle requests, assign teachers to classes
- **Teacher Dashboard**: Add students, view students, assign grades
- **Student Dashboard**: View personal report card and grades
- **CRUD Operations**: Full CRUD functionality for all entities
- **MySQL Database**: MySQL database integration with JPA/Hibernate
- **RESTful API**: Complete REST API endpoints

## Technology Stack

- **Spring Boot 3.2.0**
- **Spring Security**
- **Spring Data JPA**
- **MySQL Connector**
- **JWT (JSON Web Tokens)**
- **Gradle**
- **Java 17**

## Database Schema

### Users Table
- `id` (Primary Key)
- `username` (Unique)
- `password` (Encrypted)
- `firstName`
- `lastName`
- `role` (ADMIN/TEACHER/STUDENT)
- `status` (PENDING/APPROVED/DECLINED)
- `assignedClass`
- `createdAt`
- `updatedAt`

### Students Table
- `id` (Primary Key)
- `name`
- `section`
- `userId` (Foreign Key to Users)
- `createdAt`
- `updatedAt`

### Subjects Table
- `id` (Primary Key)
- `name`
- `description`
- `createdAt`
- `updatedAt`

### Grades Table
- `id` (Primary Key)
- `studentId` (Foreign Key to Students)
- `subjectId` (Foreign Key to Subjects)
- `gradeValue` (0.0 - 100.0)
- `dateAssigned`
- `createdAt`
- `updatedAt`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/signup` - User registration

### Admin Endpoints
- `GET /api/users/teachers` - Get all teachers
- `GET /api/users/pending` - Get pending user requests
- `PUT /api/users/{id}/approve` - Approve user
- `PUT /api/users/{id}/decline` - Decline user
- `DELETE /api/users/{id}` - Delete user
- `PUT /api/users/{id}/assign` - Assign teacher to class

### Teacher Endpoints
- `GET /api/students` - Get all students
- `POST /api/students` - Add new student
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student
- `GET /api/subjects` - Get all subjects
- `POST /api/grades` - Assign grade
- `PUT /api/grades/{id}` - Update grade
- `DELETE /api/grades/{id}` - Delete grade
- `GET /api/grades/student/{id}` - Get grades for student

### Student Endpoints
- `GET /api/users/current` - Get current user info
- `GET /api/students/user/{userId}` - Get student by user ID
- `GET /api/grades/student/{id}/report` - Get report card

## Setup Instructions

### 1. Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Gradle (or use Gradle wrapper)

### 2. Database Setup

1. **Install MySQL**: Make sure MySQL is installed and running
2. **Create Database**:
   ```sql
   CREATE DATABASE grade_portal_db;
   ```
3. **Update Database Credentials** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

### 3. Project Setup

1. **Clone/Navigate to the project directory**
2. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```
   Or on Windows:
   ```cmd
   gradlew.bat bootRun
   ```

3. **The application will**:
   - Automatically create the database tables
   - Start on port 8080
   - Be ready to accept API requests

### 4. Default Admin User

The application will create a default admin user on first run:
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: ADMIN
- **Status**: APPROVED

**⚠️ Important**: Change the default admin password after first login!

### 5. Initial Setup Steps

1. **Start the backend server**
2. **Login as admin** using the default credentials
3. **Create subjects** through the API or database
4. **Approve teacher and student accounts** through the admin dashboard

## Sample Data

### Default Subjects (can be added manually via API or database)
- Mathematics
- English
- Science
- History
- Physical Education

### Default Admin User
```json
{
  "username": "admin",
  "password": "admin123",
  "firstName": "System",
  "lastName": "Administrator",
  "role": "ADMIN"
}
```

## API Usage Examples

### User Registration
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher1",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "role": "TEACHER"
  }'
```

### User Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### Create Subject (Admin)
```bash
curl -X POST http://localhost:8080/api/subjects \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mathematics",
    "description": "Basic Mathematics Course"
  }'
```

## Security Features

- **Password Encryption**: BCrypt password hashing
- **JWT Authentication**: Stateless token-based authentication
- **Role-Based Access Control**: Different permissions for each role
- **CORS Configuration**: Cross-origin request support
- **Input Validation**: Request validation and sanitization

## Role Permissions

### Admin
- View and manage all teachers
- Handle user requests (approve/decline)
- Assign teachers to classes
- Create and manage subjects
- View all data

### Teacher
- Add and manage students
- Assign grades to students
- View all students and their grades
- Create report cards

### Student
- View personal report card
- View only their own grades
- Access personal statistics

## Development

### Building the Project
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### Clean and Rebuild
```bash
./gradlew clean build
```

## Troubleshooting

### Database Connection Issues
- Verify MySQL is running
- Check database credentials in `application.properties`
- Ensure MySQL connector is in the classpath
- Check MySQL user permissions

### JWT Token Issues
- Verify JWT secret key is configured
- Check token expiration settings
- Ensure proper Authorization header format

### CORS Issues
- Verify CORS configuration in `SecurityConfig.java`
- Check allowed origins in `application.properties`

## Production Considerations

1. **Change default admin password**
2. **Use environment variables for sensitive data**
3. **Configure proper CORS origins**
4. **Enable HTTPS**
5. **Set up proper logging and monitoring**
6. **Configure database connection pooling**
7. **Use production-grade MySQL configuration**

## Support

For issues and questions:
1. Check the application logs
2. Verify database connection
3. Test API endpoints with tools like Postman
4. Ensure all required dependencies are installed

## License

This project is created for educational purposes.
