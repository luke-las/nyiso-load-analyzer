package com.example;

import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Component;

@Component
public class LoadDataImporter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    private static final Path basePath = Paths.get("C:\\DEV\\Powershell_scripts\\downloaded_files");

    public void run() throws Exception {
        // Example: call importDirectory with no dates to skip processing
        importDirectory(basePath, null, null);
    }

    public Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/my_data_db";
        String user = "postgres";
        String pass = "password";
        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Import CSV files filtering records by optional date range.
     * @param basePath Directory containing CSV files
     * @param startDate Inclusive start date (LocalDateTime) or null to skip filtering start
     * @param endDate Inclusive end date (LocalDateTime) or null to skip filtering end
     */
    public void importDirectory(Path basePath, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        // If no date range provided, skip import entirely
        if (startDate == null || endDate == null) {
            System.out.println("No date range provided; skipping CSV import.");
            return;
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO load_data (timestamp, zone, load_value) VALUES (?, ?, ?) " +
                "ON CONFLICT (timestamp, zone) DO UPDATE SET load_value = EXCLUDED.load_value")) {

                Files.walk(basePath)
                    .filter(p -> p.toString().endsWith(".csv"))
                    .forEach(csvPath -> {
                        try {
                            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                                .withFirstRecordAsHeader()
                                .parse(Files.newBufferedReader(csvPath));

                            for (CSVRecord record : records) {
                                String zone = record.get("Name");
                                String timestampStr = record.get("Time Stamp");
                                String loadStr = record.get("Load");

                                LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);

                                // Skip records outside date range
                                if (timestamp.isBefore(startDate) || timestamp.isAfter(endDate)) {
                                    continue;
                                }

                                double load = Double.parseDouble(loadStr);

                                ps.setTimestamp(1, Timestamp.valueOf(timestamp));
                                ps.setString(2, zone);
                                ps.setDouble(3, load);
                                ps.addBatch();
                            }

                            ps.executeBatch();
                            conn.commit();
                            System.out.println("Processed file: " + csvPath.getFileName());
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                conn.rollback();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
            }
        }
    }
}
