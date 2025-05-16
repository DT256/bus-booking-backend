# Bus Booking Backend

A Spring Boot backend application for managing bus booking operations. This system provides RESTful APIs for handling bus routes, schedules, bookings, and user management.

## 🚀 Features

- User Authentication and Authorization
- Bus Route Management
- Schedule Management
- Booking System
- Payment Integration
- User Profile Management
- Admin Dashboard APIs

## 🛠️ Technology Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data MongoDB
- MongoDB Database
- Maven
- JWT Authentication

## 📋 Prerequisites

- JDK 17 or higher
- Maven 3.6 or higher
- MongoDB 4.4 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🔧 Installation

1. Clone the repository:
```bash
git clone https://github.com/your-username/bus-booking-backend.git
cd bus-booking-backend
```

2. Configure the database:
   - Start MongoDB service
   - Update `application.properties` with your MongoDB connection details

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📁 Project Structure

```
src/main/java/com/group8/busbookingbackend/
├── controller/     # REST API endpoints
├── service/        # Business logic layer
├── repository/     # Data access layer
├── entity/         # Database entities
├── dto/            # Data Transfer Objects
├── mapper/         # Object mapping classes
├── config/         # Configuration classes
├── security/       # Security related classes
├── exception/      # Custom exception handling
├── helper/         # Helper/utility classes
└── utils/          # Utility functions
```

## 🔐 API Documentation

The API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- Bui Duc Thang
- Nguyen Hoai Tan

## 🙏 Acknowledgments

- Spring Boot Team
- All contributors who have helped shape this project 