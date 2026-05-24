# Personal Finance Manager API

A comprehensive RESTful web application built with Spring Boot that enables users to track their income, expenses, and savings goals securely.

## 🚀 Features
* **User Authentication:** Secure, session-based authentication with encrypted passwords.
* **Data Isolation:** Complete data segregation ensuring users can only access their own financial records.
* **Category Management:** Support for global default categories (Salary, Food, Rent, etc.) and user-defined custom categories.
* **Transaction Tracking:** Full CRUD operations for financial transactions with strict validation constraints.
* **Dynamic Savings Goals:** Real-time progress calculation based on transaction history.
* **Financial Analytics:** Automated aggregation for monthly and yearly financial reports.

## 🛠️ Technology Stack
* **Language:** Java 17
* **Framework:** Spring Boot 3.x
* **Security:** Spring Security (Session-based)
* **Database:** H2 (In-Memory)
* **Build Tool:** Maven

## 📐 Architecture & Design Decisions
* **Layered Architecture:** Follows a strict Controller → Service → Repository pattern for clean separation of concerns.
* **Data Transfer Objects (DTOs):** Uses DTOs to decouple incoming API request shapes from internal database entities, allowing for flexible partial updates.
* **Global Exception Handling:** Implemented via `@ControllerAdvice` to intercept standard exceptions and validation failures, ensuring the API always returns structured JSON error messages (400, 401, 403, 404, 409).
* **Dynamic Calculations:** Savings Goal progress and Report aggregations are calculated dynamically at runtime to ensure data consistency.

## ⚙️ Local Setup Instructions
1. Clone the repository: `git clone https://github.com/shreyas-1727/personal-finance-manager-api`
2. Open the cloned `manager` directory in your preferred Java IDE (VS Code, IntelliJ, Eclipse).
3. Allow the IDE to download the required Maven dependencies.
4. Locate the main application class (usually `ManagerApplication.java` inside `src/main/java/...`) and run it using your IDE's "Run" or "Play" button.
5. The server will start on `http://localhost:8080`.
6. Access the H2 Database console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:financedb`, User: `sa`, Password: `[blank]`).

## 📚 API Documentation
* **Auth:** `/api/auth/register`, `/api/auth/login`, `/api/auth/logout`
* **Categories:** `/api/categories` (GET, POST, DELETE)
* **Transactions:** `/api/transactions` (GET, POST, PUT, DELETE)
* **Goals:** `/api/goals` (GET, POST, PUT, DELETE)
* **Reports:** `/api/reports/monthly/{year}/{month}`, `/api/reports/yearly/{year}`

## 🌐 Live Deployment
The API is publicly deployed and accessible at:
**https://personal-finance-manager-api-p7y6.onrender.com/api**

*Note: Since the application utilizes an in-memory H2 database, all data is reset when the server restarts.*