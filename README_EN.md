# CMMS - Construction Materials Management System

**Sài Gòn CMC - Construction Materials Management System (CMMS)** is a comprehensive enterprise digitalization solution specifically engineered for building materials distribution and supply chain operations. Built on **Spring Boot 4** and **Java 26**, the system solves complex real-world supply chain challenges that traditional retail or generic e-commerce platforms cannot address.

---

## 🛠️ Technologies Used

This project utilizes a modern enterprise-grade technology stack for maximum performance, security, and scalability:

*   **Programming Language:** Java 26 (employing advanced language features for peak efficiency).
*   **Core Frameworks:** Spring Boot 4.0.6 (Spring Web, Spring Data JPA, Spring Security 6).
*   **Database Engine:** MySQL 8.0+ (relational schema design mapped using Hibernate 7 ORM).
*   **Security:** Spring Security 6 (Role-Based Access Control - RBAC, endpoint authorization, and CSRF protection).
*   **Template Engine:** Thymeleaf 3.0 (integrated with `thymeleaf-extras-springsecurity6` for dynamic UI access control).
*   **Frontend UI & Styling:** Premium Vanilla CSS (using variables, Grid/Flexbox layouts, and glassmorphism styling) ensuring a smooth responsive design on all devices.
*   **Artificial Intelligence:** Secure Java backend RestController proxying requests to the Google Gemini 2.5 Flash API.
*   **Dependency Management & Build:** Maven.

## 🚀 Real-World Business Cases Supported

Unlike general retail software, this application is custom-tailored to the operational patterns of heavy construction material supply:

### 1. Long-term Supply Contracts & Fixed Pricing
*   **Real-world Problem:** The market prices of building materials (steel, cement, aggregates) fluctuate daily. Contractors require guaranteed fixed prices throughout a project's timeline (usually 6–12 months) to prevent budget overruns.
*   **System Solution:** The system supports **Long-term Project Contracts** (`HopDong`) defining fixed unit prices for specific materials. When dispatching items to active sites via retail orders (`DonHang`), the pricing engine automatically fetches contract rates instead of floating market retail rates.

### 2. Strict Credit Limit Enforcement
*   **Real-world Problem:** Contractors buy materials in large volumes on credit. Without automation, sales reps might continue shipping materials to defaulting contractors, resulting in massive bad debts.
*   **System Solution:** Every customer (`KhachHang`) has a specific credit threshold (`hanMucNo`). Before warehouse keepers dispatch a shipment, the system calculates: **(Active Liabilities + New Order Value) > Allowed Limit**. If breached, the order is blocked automatically, requiring manual override by the accounting department or debt clearance by the customer.

### 3. Split Responsibility in Warehousing & Logistics
*   **Real-world Problem:** Discrepancies between inventory ledgers and physical stocks are common due to natural handling waste or errors during bulk loading.
*   **System Solution:** The system separates the role of **Sales Representative** (drafts order) and **Warehouse Keeper** (authorizes dispatch). Active stock levels (`TonKho`) are decremented only when the Warehouse Keeper signs off on the physical loading voucher (`PhieuKho`).

### 4. Surplus Returns & Liabilities Clearance
*   **Real-world Problem:** Upon project completion, contractors often have leftover steel bars or bricks. They expect to return these surplus materials to the supplier and deduct the value from their outstanding balance.
*   **System Solution:** The system processes returns (`DoiTraHang`), automatically restocks the physical warehouse, and creates a liability offset voucher to deduct the returned value from the contractor's outstanding ledger.

### 5. Standard Construction Estimation Engine
*   Estimates material requirement volumes based on total floor area (m²), structural framework type (residential, villa, townhouse), and floor levels.
*   Utilizes national building code coefficients (`DinhMucVatLieu`) to calculate required cement (bags), sand (m³), gravel (m³), bricks (count), and reinforcement steel (kg).

---

## 🛠️ Technology Stack & Package Architecture

### 1. Technology Core
*   **Backend Framework:** Spring Boot 4.0.6, Spring Security 6, Spring Data JPA (Hibernate 7)
*   **Database:** MySQL 8.0+
*   **Frontend UI:** Thymeleaf, CSS Grid/Flexbox with glassmorphism layout, fully responsive.
*   **Artificial Intelligence:** Secure Java RestController routing to Google Gemini 2.5 Flash API.

### 2. Domain Sub-packaging Structure
The application code is organized cleanly into business domain folders inside the primary Spring layers:
```
com.example.ht_vlxd
├── Config                  # Configurations
│   ├── auth                # Spring Security rules
│   └── common              # System seeding (DatabaseSeeder)
├── Controller              # REST / View Controllers
│   ├── auth / customer     # Authentication & profiles
│   ├── inventory / sales   # Warehousing, orders, contracts
│   ├── finance / estimation# Ledgers, calculator endpoints
│   └── ai                  # Gemini AI Chat wrapper
├── DTO                     # Data Transfer Objects
├── Model                   # 20 JPA Database Entities
├── Repository              # Spring Data JPA Interfaces
└── Service                 # Business Logic Services
```

---

## 🔑 Demo Access Credentials

To test the role-based workflows, use the accounts below (default password is `Admin@123`):

*   **Administrator (`admin`):** General configurations, user management, and roles.
*   **Director (`giamdoc`):** Financial reporting dashboards and overall credit overview.
*   **Sales Representative (`nvkd01`):** Drafts project contracts and sales orders.
*   **Warehouse Keeper (`nvkho01`):** Authorizes dispatch slips and oversees active stocks.
*   **Accountant (`nvkt01`):** Monitors liability ledgers, settles invoices, and reviews credit blocks.
*   **Customer (`khachhang01`):** Runs material estimations, places online orders, and consults the AI assistant.

---

## ⚙️ Quick Installation Guide

1.  **Setup Database & Gemini API Key:** Configure your local MySQL credentials and **Gemini API Key** in [application.properties](file:///D:/Construction_materials_management_system-feature-may-tinh-vat-lieu-1/Construction_materials_management_system-feature-may-tinh-vat-lieu-1/ht_vlxd/src/main/resources/application.properties):
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/vlxd_db?createDatabaseIfNotExist=true
    spring.datasource.username=root
    spring.datasource.password=your_password
    gemini.api.key=your_gemini_api_key
    ```
2.  **Launch application:** Run the following command in your terminal:
    ```powershell
    .\mvnw.cmd spring-boot:run
    ```
3.  **View Website:** Navigate to `http://localhost:8080` in your web browser.
