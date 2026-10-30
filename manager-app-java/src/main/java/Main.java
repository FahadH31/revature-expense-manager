import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.*;

import db.Database;
import menu.Menu;
import model.User;

public class Main {
    private static final Logger LOGGER =
            Logger.getLogger("ManagerApp");

    public static void main(String[] args) {
        try {
            configureLogging();
            try (Connection conn = Database.connect(Database.dbPath())) {
                Menu menu = new Menu();

                User manager = menu.runLogin(conn);
                menu.runMainMenu(manager, conn);
            }
        } catch (IOException | SQLException e) {
            System.err.println("Unable to start the manager app.");
            System.exit(1);
        }
    }

    private static void configureLogging() throws IOException {
        Files.createDirectories(Path.of("logs"));

        FileHandler fh =
                new FileHandler("logs/manager_app.log", true);

        fh.setFormatter(new SimpleFormatter());
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(fh);
        LOGGER.setLevel(Level.INFO);
    }
}