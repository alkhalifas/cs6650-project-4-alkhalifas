package client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Client logger that logs the messages from the system with the date and time
 */
public class ClientLogger {
    public static void log(String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        System.out.println(formattedDateTime + " - " + message);
    }
}
