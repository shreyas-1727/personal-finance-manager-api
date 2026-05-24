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

## ⚙️ Local Setup Instructions
1. Clone the repository: `git clone [YOUR_GITHUB_REPO_LINK]`
2. Navigate into the project directory: `cd manager`
3. Run the application using the Maven wrapper: `./mvnw spring-boot:run`
4. The server will start on `http://localhost:8080`.
5. Access the H2 Database console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:financedb`, User: `sa`, Password: `[blank]`).

## 📚 API Documentation
* **Auth:** `/api/auth/register`, `/api/auth/login`, `/api/auth/logout`
* **Categories:** `/api/categories` (GET, POST, DELETE)
* **Transactions:** `/api/transactions` (GET, POST, PUT, DELETE)
* **Goals:** `/api/goals` (GET, POST, PUT, DELETE)
* **Reports:** `/api/reports/monthly/{year}/{month}`, `/api/reports/yearly/{year}`

## 🌐 Live Deployment
The API is publicly deployed and accessible at:
**[YOUR_LIVE_RENDER_URL_HERE]/api**

*Note: Since the application utilizes an in-memory H2 database, all data is reset when the server restarts.*