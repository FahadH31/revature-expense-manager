package db;

import org.sqlite.SQLiteConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {

    public static Connection connect(String path) throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setBusyTimeout(5000);
        return DriverManager.getConnection("jdbc:sqlite:" + path, config.toProperties());
    }

    public static String dbPath() {
        String path = System.getenv("EM_DB_PATH");
        if (path == null || path.isBlank()) {
            System.out.println("Error: EM_DB_PATH environment variable is not set.");
            System.exit(1);
        }
        return path;
    }
}
// Usage: try (Connection conn = Database.connect(Database.dbPath())) { ... }
