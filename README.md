NYISO Load Analyzer
A load data visualization and analysis tool built with Java Spring Boot backend and React frontend.
Supports importing historical load CSV data, querying by date and zone, and visualizing load trends and comparisons.

Features
Import and parse load CSV files filtered by date range

Store data in PostgreSQL database

REST API to serve load data and comparison data

Interactive React UI with load charts and comparison charts

Configurable data source directory

Prerequisites
Java 17+ (tested with OpenJDK 17)

Maven 3.6+

Node.js 18+ and npm/yarn

PostgreSQL 12+ database running locally or accessible remotely

Setup Instructions
1. Clone repository
2. Configure PostgreSQL database
   Create a database (default name: my_data_db)
   Create a user with password and grant privileges
   Import table schema:
     sql
     Copy
     Edit
     CREATE TABLE load_data (
        timestamp TIMESTAMP NOT NULL,
        zone VARCHAR NOT NULL,
        load_value DOUBLE PRECISION,
        PRIMARY KEY (timestamp, zone)
     );
   Update database credentials if needed in backend/src/main/resources/application.properties:

3. Data download
   The powershell script can be used to download data from NYISO (https://mis.nyiso.com/public/P-58Blist.htm)
   In the CLI: .\loadDataDownloader.ps1 -StartDate ([datetime]"2023-07-01T00:00:00") -EndDate ([datetime]"2025-07-01T23:59:59")
   This will download records from 2023-07 to 2025-07

4. Import Data
  On backend startup, the app attempts to import data based on provided start and end date arguments. For example:
  mvn spring-boot:run -D"spring-boot.run.arguments=--start=2023-07-01T00:00:00 --end=2025-07-01T23:59:59"



  If no dates are provided, import will be skipped. If no data is imported into the DB, then the charts will not load.

6. Backend Setup and Run
  mvn clean install
  mvn spring-boot:run
  The backend REST API will start at http://localhost:8080.

7. Frontend Setup and Run
  Open a new terminal window/tab:
  cd frontend
  npm install
  npm start
  The React app will open in your browser at http://localhost:3000.
