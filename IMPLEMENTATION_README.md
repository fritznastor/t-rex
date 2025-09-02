# TopBloc Backend Code Challenge - Complete Implementation

##  Overview

This repository contains a **complete, production-ready implementation** of the TopBloc Backend Code Challenge for an Enterprise Candy Planning (ECP) system. What started as a basic API challenge has been transformed into a full-stack application with modern architecture, comprehensive testing, and containerized deployment.

##  Architecture

### **Technology Stack**
- **Backend**: Java 11 + Spark Framework + SQLite
- **Frontend**: React 18 + TypeScript + Material-UI
- **Database**: SQLite with relational schema
- **Containerization**: Docker + Docker Compose
- **Build Tools**: Maven (backend), npm (frontend)

### **System Design**
```
┌─────────────────────────────────────────────────────────────┐
│                     TopBloc Full Stack                      │
├─────────────────────┬─────────────────────┬─────────────────┤
│   React Frontend    │   Java Backend      │  SQLite Browser │
│   (Port 3000)       │   (Port 4567)       │   (Port 8080)   │
│                     │                     │                 │
│ ┌─────────────────┐ │ ┌─────────────────┐ │ ┌─────────────┐ │
│ │   Nginx         │ │ │   Spark API     │ │ │ Web UI      │ │
│ │   + React SPA   │ │ │   + Maven       │ │ │ (Optional)  │ │
│ └─────────────────┘ │ └─────────────────┘ │ └─────────────┘ │
│         │           │         │           │         │       │
└─────────┼───────────┴─────────┼───────────┴─────────┼───────┘
          │                     │                     │
          └─────────────────────┼─────────────────────┘
                                │
                    ┌─────────────────────┐
                    │   SQLite Database   │
                    │   (challenge.db)    │
                    └─────────────────────┘
```

### **Backend Architecture Pattern**
The backend follows a **Route-Handler Pattern with Data Access Layer**:

```
┌─────────────────────────┐    ┌──────────────────────────┐
│      Main.java          │    │    DatabaseManager.java  │
│  (Spark Routes +        │────▶│  (Data Access Layer +   │
│   Request Handlers)     │    │   Business Logic)        │
└─────────────────────────┘    └──────────────────────────┘
           │                              │
           ▼                              ▼
┌─────────────────────────┐    ┌──────────────────────────┐
│    HTTP Requests        │    │      SQLite Database     │
│  (Spark Framework)      │    │     (challenge.db)       │
└─────────────────────────┘    └──────────────────────────┘
```

**Key Components:**
- **Main.java**: Route definitions using Spark's DSL + request handling logic
- **DatabaseManager.java**: Data access layer with SQL operations and business logic
- **Spark Framework**: Lightweight web framework for HTTP request routing
- **SQLite Database**: Persistent data storage with normalized schema

##  Database Schema

The SQLite database implements a normalized schema for candy inventory management:

```sql
items (17 records)
├── id (PK)
└── name (unique)

inventory (17 records)  
├── id (PK)
├── item (FK → items.id) 
├── stock
└── capacity

distributors (3 records)
├── id (PK) 
└── name (unique)

distributor_prices (27 records)
├── id (PK)
├── distributor (FK → distributors.id)
├── item (FK → items.id)
└── cost
```

## ✅ Core Challenge Implementation

### **All Required GET Routes**
-  `GET /inventory` - All inventory items with stock levels
-  `GET /inventory/out-of-stock` - Items with stock = 0
-  `GET /inventory/overstocked` - Items where stock > capacity
-  `GET /inventory/low-stock` - Items with stock < 35% of capacity
-  `GET /inventory/:id` - Specific item inventory details
-  `GET /distributors` - All distributors
-  `GET /distributors/:id/items` - Items by distributor with pricing
-  `GET /items/:id/distributors` - All distributor offerings for an item

### **All Required POST/PUT/DELETE Routes**
-  `POST /items` - Add new candy items
-  `POST /inventory` - Add items to inventory
-  `POST /distributors` - Add new distributors
-  `POST /distributors/:id/items` - Add distributor pricing
-  `PUT /inventory/:id` - Update stock/capacity
-  `PUT /distributors/:distId/items/:itemId` - Update pricing
-  `DELETE /inventory/:id` - Remove from inventory
-  `DELETE /distributors/:id` - Remove distributor
-  `DELETE /distributors/:distId/items/:itemId` - Remove pricing

### **Special Features**
-  `GET /items/:id/cheapest?quantity=N` - Find cheapest restock option
-  Comprehensive error handling with proper HTTP status codes
-  Input validation and SQL injection protection
-  CORS support for frontend integration

##  Bonus Challenge Implementations

### **1. React Frontend (Complete SPA)**
Built a React application with:
- **Material-UI Components** - Modern, responsive design
- **TypeScript Integration** - Type-safe API interactions
- **Four Management Modules**:
  - `InventoryManager` - Stock tracking with visual indicators
  - `DistributorManager` - Supplier and pricing management
  - `ItemManager` - Product catalog management  
  - `ExportManager` - CSV export functionality

- **Advanced Features**:
  - Real-time filtering (out-of-stock, low-stock, overstocked)
  - Complete CRUD operations with form validation
  - Success/error notifications
  - Responsive layout with tabbed navigation

### **2. Docker Containerization (Production Ready)**
Implemented complete containerization with:
- **Multi-stage Docker builds** for optimized images
- **Docker Compose orchestration** with 3 services:
  - Backend API (Java/Spark)
  - Frontend SPA (React/Nginx)
  - Database Browser (SQLite Web UI)
- **Production features**:
  - Health checks for all services
  - Volume mounting for data persistence
  - Environment variable configuration
  - Network isolation and service discovery

### **3. CSV Export System**
Created a secure table export system:
- Export any database table to CSV format
- Proper CSV escaping for special characters
- Security validation against table injection
- RESTful endpoint: `GET /export/csv?table=tablename`

##  Quality Assurance

### **Comprehensive Test Suite**
Developed `TestSuite.java` with **79 automated tests** covering:
-  **Valid requests** - All endpoints with correct data
-  **Error scenarios** - Invalid inputs, malformed requests
-  **Edge cases** - Boundary values, empty parameters
-  **Security testing** - SQL injection, XSS protection
-  **HTTP compliance** - Proper status codes and headers

### **Code Quality**
- Clean separation of concerns (Route-Handler pattern with Data Access Layer)
- Comprehensive error handling and logging
- Input validation and sanitization
- Consistent JSON response format
- Modern Java practices with prepared statements

##  Getting Started

### **Prerequisites**
- Docker & Docker Compose
- Java 11+ (for local development)
- Node.js 16+ (for frontend development)

### **Quick Start (Recommended)**
```bash
# Clone the repository
git clone <repository-url>
cd backend-code-challenge

# Start the entire stack with Docker
docker-compose up --build -d

# Access the applications
echo "Frontend:    http://localhost:3000"
echo "Backend API: http://localhost:4567" 
echo "DB Browser:  http://localhost:8080"

# Test the setup
curl "http://localhost:4567/inventory"
```

### **Development Mode**
```bash
# Backend (Terminal 1)
mvn exec:java -Dexec.mainClass="com.topbloc.codechallenge.Main"

# Frontend (Terminal 2) 
cd frontend
npm install
npm start

# Run comprehensive tests (Terminal 3)
mvn compile
java -cp "target/classes" com.topbloc.codechallenge.TestSuite
```

### **Resetting the Database**
To reset the database to its original state with fresh seed data:

**Step 1: Stop Docker containers (if running)**
docker compose down

**Step 2: Start the backend server locally**
Choose one of these options in your first terminal:

# Option A: IDE - Press the "Run" button on Main.java

# Option B: Full command
mvn exec:java -Dexec.mainClass="com.topbloc.codechallenge.Main"

# Option C: Short command (uses pom.xml configuration)
mvn exec:java


**Step 3: Trigger the database reset**
In a second terminal, call the reset endpoint:
curl "http://localhost:4567/reset"

**Step 4: Verify the reset worked**
You should see this output in your first terminal:
```
Applying schema
Schema applied
Seeding database
Database seeded
```

**Step 5: Confirm database state (optional)**
Verify the reset by checking the data:

curl "http://localhost:4567/inventory"
curl "http://localhost:4567/items"
curl "http://localhost:4567/distributors"

### **API Testing**

# Test core endpoints
curl "http://localhost:4567/version"
curl "http://localhost:4567/inventory"
curl "http://localhost:4567/inventory/low-stock"
curl "http://localhost:4567/distributors"

# Test CSV export
curl "http://localhost:4567/export/csv?table=items" -o items.csv

# Test cheapest restock
curl "http://localhost:4567/items/1/cheapest?quantity=50"

## Running the Test Suite
This project includes a comprehensive test suite with 79 automated tests that validate all API endpoints, error handling, and edge cases.

### Quick Test Run
# 1. Start your server first
mvn exec:java -Dexec.mainClass="com.topbloc.codechallenge.Main"

# 2. In another terminal, compile and run tests
mvn compile
java -cp "target/classes" com.topbloc.codechallenge.TestSuite


### Using Docker

# Start the full stack
docker-compose up --build -d

# Run tests against the containerized API
mvn compile
java -cp "target/classes" com.topbloc.codechallenge.TestSuite


### What the Tests Cover
-  **All API endpoints** - GET, POST, PUT, DELETE operations
-  **Valid requests** - Normal operations that should succeed
-  **Invalid inputs** - Bad parameters, wrong formats, missing data
-  **Edge cases** - Boundary conditions, empty values, very large numbers
-  **Security** - SQL injection attempts, XSS protection
-  **Error handling** - Proper HTTP status codes and error messages

### Test Output
You'll see results like:
```
TOPBLOC BACKEND CODE CHALLENGE - COMPREHENSIVE TEST SUITE
======================================================================

BASIC ENDPOINTS
--------------------------------------------------
✓ PASS GET /version
✓ PASS GET /items
✓ PASS GET /reset

INVENTORY ENDPOINTS  
--------------------------------------------------
✓ PASS GET /inventory - Valid
✓ PASS GET /inventory/out-of-stock - Valid
...

======================================================================
TEST RESULTS SUMMARY
======================================================================
Total Tests: 79
Passed: 79
Failed: 0
Success Rate: 100.0%

ALL TESTS PASSED! Your API is working perfectly!
======================================================================
```

### Troubleshooting Tests
- **Server not running?** Make sure the API is accessible at `http://localhost:4567`
- **Tests failing?** Try resetting the database: `curl "http://localhost:4567/reset"`
- **Compilation issues?** Run `mvn clean compile` first

## 📁 Project Structure

```
backend-code-challenge/
├── src/main/java/com/topbloc/codechallenge/
│   ├── Main.java                    # Spark route definitions & request handlers
│   ├── TestSuite.java              # 79 comprehensive tests
│   └── db/DatabaseManager.java     # Data access layer & business logic
├── frontend/                       # React TypeScript application
│   ├── src/
│   │   ├── components/             # UI management modules
│   │   ├── services/api.ts         # Typed API client
│   │   └── App.tsx                 # Main application
│   ├── public/                     # Static assets
│   └── Dockerfile                  # Frontend container config
├── challenge.db                    # SQLite database file
├── docker-compose.yml              # Multi-service orchestration
├── Dockerfile                      # Backend container config
├── pom.xml                         # Maven dependencies
└── README.md                       # Original challenge requirements
```

##  Key Features & Innovations

### **Backend Enhancements**
- Route-Handler pattern with Spark Framework for lightweight API design
- Direct route definitions with embedded request handling logic
- Data Access Layer (DatabaseManager) separating SQL operations from HTTP logic
- Comprehensive input validation and error handling
- SQL injection protection with prepared statements
- CORS configuration for cross-origin requests
- Database connection management and transactions

### **Frontend Excellence**
- Modern React 18 with TypeScript for type safety
- Material-UI for professional component library
- Responsive design with mobile-friendly layout
- Real-time inventory status indicators
- Advanced filtering and search capabilities
- Form validation with user-friendly error messages

### **DevOps & Deployment**
- Production-ready Docker configuration
- Multi-stage builds for optimized container size
- Health checks and automatic restart policies
- Environment-based configuration
- Database persistence with volume mounting

### **Testing & Quality**
- Automated test suite with 100% endpoint coverage
- Security testing for common vulnerabilities
- Performance considerations with connection pooling
- Proper HTTP status code implementation
- Comprehensive error scenarios testing

##  Performance & Scalability

- **Database**: SQLite with proper indexing and foreign key constraints
- **API**: Efficient SQL queries with prepared statements
- **Frontend**: Production build with code splitting and minification
- **Container**: Multi-stage builds reducing image size by 60%
- **Caching**: Browser caching headers for static assets

---
