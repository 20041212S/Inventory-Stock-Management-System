# Inventory Stock Management System

A comprehensive web-based Inventory Stock Management System built with React.js frontend and Spring Boot backend, using PostgreSQL as the database.

## Technology Stack

- **Frontend**: React.js 18.2.0
- **Backend**: Spring Boot 3.2.0
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Testing**: JUnit 5

## Features

### Part Master Management
- Import part master data from Excel files
- View all parts in a paginated table
- Search functionality (by Part Number, Part Name, or Category)
- Display part details including:
  - Part Number (Primary Key)
  - Part Name
  - Make or Buy
  - Category and Subcategory
  - Unit of Measure
  - Drawing Number, Revision Number, Revised Date
  - Location

### Stock Management
- CRUD operations for stock entries
- Manual stock entry with part selection
- Stock fields: Quantity, Bin Number, Rack Number
- Automatic timestamp tracking for last update
- Validation: Only parts from master data can be added to stock

### Bulk Stock Upload
- Excel template download for bulk upload
- Bulk upload with validation:
  - Aggregates stock quantities for duplicate part numbers
  - Warns if different bin numbers detected for same part
  - Rejects records with invalid part numbers
- Detailed upload response with success/failure counts and messages

### Export Functionality
- Export stock data to Excel (.xlsx)
- Export stock data to PDF
- Download stock upload template

### Search and Pagination
- Search parts by Part Number, Part Name, or Category
- Pagination for both Part Master and Stock views
- Separate window/view for stock details

## Project Structure

```
Stock management/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/edacmulttech/inventory/
│   │   │   │   ├── config/          # Configuration classes
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── entity/          # JPA entities
│   │   │   │   ├── exception/       # Exception handlers
│   │   │   │   ├── repository/      # JPA repositories
│   │   │   │   └── service/          # Business logic
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                     # Unit tests
│   └── pom.xml
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/              # React components
│   │   ├── services/                # API service layer
│   │   ├── App.js
│   │   └── index.js
│   └── package.json
└── README.md
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 14+ and npm
- PostgreSQL 12+

## Setup Instructions

### Database Setup

1. Install PostgreSQL if not already installed
2. Create a new database:
```sql
CREATE DATABASE inventory_db;
```

3. Update database credentials in `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## API Documentation

### Part Master APIs

#### Get All Parts (Paginated)
```
GET /api/parts?page=0&size=10&search=term
```

#### Get All Parts (List)
```
GET /api/parts/all
```

#### Get Part by Part Number
```
GET /api/parts/{partNumber}
```

#### Import Parts from Excel
```
POST /api/parts/import
Content-Type: multipart/form-data
Body: file (Excel file)
```

### Stock APIs

#### Get All Stocks (Paginated)
```
GET /api/stocks?page=0&size=10
```

#### Get All Stocks (List)
```
GET /api/stocks/all
```

#### Get Stock by ID
```
GET /api/stocks/{stockId}
```

#### Get Stocks by Part Number
```
GET /api/stocks/part/{partNumber}
```

#### Create Stock
```
POST /api/stocks
Content-Type: application/json
Body: {
  "partNumber": "string",
  "stockQuantity": number,
  "binNumber": "string",
  "rackNumber": "string"
}
```

#### Update Stock
```
PUT /api/stocks/{stockId}
Content-Type: application/json
Body: {
  "partNumber": "string",
  "stockQuantity": number,
  "binNumber": "string",
  "rackNumber": "string"
}
```

#### Delete Stock
```
DELETE /api/stocks/{stockId}
```

#### Bulk Upload Stocks
```
POST /api/stocks/bulk-upload
Content-Type: multipart/form-data
Body: file (Excel file)
```

#### Download Stock Upload Template
```
GET /api/stocks/template/download
```

#### Export Stocks to Excel
```
GET /api/stocks/export/excel
```

#### Export Stocks to PDF
```
GET /api/stocks/export/pdf
```

### Location APIs

#### Get All Locations
```
GET /api/locations
```

#### Create Location
```
POST /api/locations
Content-Type: application/json
Body: {
  "locationName": "string",
  "description": "string"
}
```

## Excel File Format

### Part Master Import Format

| Part Number | Part Name | Make or Buy | Category | Subcategory | Unit of Measure | Drawing Number | Revision Number | Revised Date | Location |
|-------------|-----------|-------------|----------|-------------|-----------------|----------------|-----------------|--------------|----------|

### Stock Upload Template Format

| Part Number | Stock | Bin | Rack |
|-------------|-------|-----|------|

## Testing

### Backend Tests

Run all backend tests:
```bash
cd backend
mvn test
```

### Frontend Tests

Run frontend tests:
```bash
cd frontend
npm test
```

## Key Features Implementation

### Clean Architecture
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Data access using Spring Data JPA

### Database Indexing
- Indexes on primary keys (Part Number)
- Indexes on foreign keys
- Indexes on frequently searched columns (Category, Bin Number, Rack Number)

### Validation
- Input validation at controller and service layers
- Part number existence validation before stock creation
- Excel file format validation

### Exception Handling
- Global exception handler for consistent error responses
- Proper error messages for different scenarios

## Usage Guide

1. **Import Part Master Data**:
   - Navigate to Part Master page
   - Click "Import from Excel"
   - Select your Excel file with part data
   - Parts will be imported and displayed

2. **Manage Stock**:
   - Navigate to Stock Management page
   - Click "Add Stock" to manually add stock entries
   - Select a part from the dropdown
   - Enter stock quantity, bin number, and rack number
   - Click "Create" to save

3. **Bulk Upload Stock**:
   - Click "Bulk Upload" button
   - Download the template Excel file
   - Fill in the stock data
   - Upload the file
   - Review the upload results

4. **Export Data**:
   - Click "Export Excel" to download stock data as Excel
   - Click "Export PDF" to download stock data as PDF

## Notes

- Part Master data can only be imported, not created manually through UI
- Stock entries require valid Part Numbers from the master data
- Bulk upload aggregates stock quantities for duplicate part numbers
- Different bin numbers for the same part will generate a warning

## Troubleshooting

### Backend won't start
- Check PostgreSQL is running
- Verify database credentials in `application.properties`
- Ensure port 8080 is not in use

### Frontend won't start
- Check Node.js and npm are installed
- Run `npm install` to install dependencies
- Ensure port 3000 is not in use

### CORS Errors
- Verify CORS configuration in `CorsConfig.java`
- Check frontend is running on `http://localhost:3000`

## License

This project is developed as part of an internship assignment.

## Author

Developed for EDACMULTTECH Technology meets Engineering

