package db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSeeder {

    public static void main(String[] args) {
        String dbPath = Database.dbPath();
        try (Connection conn = Database.connect(dbPath)) {
            resetAndSeed(conn);
            System.out.println("Database successfully re-seeded from clean sql/schema.sql and sql/seed.sql!");
        } catch (SQLException | IOException e) {
            System.err.println("Error seeding database: " + e.getMessage());
        }
    }

    public static void resetAndSeed(Connection conn) throws SQLException, IOException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = OFF;");
            stmt.execute("DROP TABLE IF EXISTS approvals;");
            stmt.execute("DROP TABLE IF EXISTS expenses;");
            stmt.execute("DROP TABLE IF EXISTS categories;");
            stmt.execute("DROP TABLE IF EXISTS users;");
            stmt.execute("PRAGMA foreign_keys = ON;");

            Path schemaPath = Files.exists(Path.of("sql/schema.sql"))
                    ? Path.of("sql/schema.sql")
                    : Path.of("../sql/schema.sql");

            Path seedPath = Files.exists(Path.of("sql/seed.sql"))
                    ? Path.of("sql/seed.sql")
                    : Path.of("../sql/seed.sql");

            executeScript(stmt, schemaPath);
            executeScript(stmt, seedPath);
        }
    }

    private static void executeScript(Statement stmt, Path scriptPath) throws IOException, SQLException {
        String content = Files.readString(scriptPath);
        for (String command : content.split(";")) {
            String trimmed = command.trim();
            if (!trimmed.isEmpty()) {
                stmt.execute(trimmed);
            }
        }
    }
}
