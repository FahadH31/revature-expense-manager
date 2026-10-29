import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

public class Main {
    private static final Logger LOGGER =
            Logger.getLogger("ManagerApp");

    public static void main(String[] args) {
        try {
            configureLogging();
            LOGGER.info("Manager app started.");
        } catch (IOException e) {
            System.err.println(
                    "Unable to initialize application logging.");
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