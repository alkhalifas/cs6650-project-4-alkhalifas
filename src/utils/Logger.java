package utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Implements a logging utility wrapping the standard Java logging framework to
 * provide a simplified interface for logging messages with timestamp precision
 * to a specified file. This class is designed to be easily integrated into
 * various projects needing file-based logging with a uniform timestamp format.
 */
public class Logger implements ILogger {
  // Logger instance from java.util.logging
  private java.util.logging.Logger logger;

  // FileHandler for writing logs to a specified file
  private FileHandler fileHandler;

  // Format for timestamp in log messages
  private static final String format = "MM-dd-yyyy HH:mm:ss.SSS";

  /**
   * Constructs a Logger instance with specified name and log file.
   *
   * @param loggerName  Name of the logger, typically related to the application module.
   * @param logFileName Path and name of the log file where messages will be written.
   */
  public Logger(String loggerName, String logFileName) {
    this.createLog(loggerName, logFileName);
  }

  /**
   * Initializes the logger and its file handler.
   * Sets up the file handler to append to the specified log file and formats log messages
   * to include a precise timestamp followed by the message content.
   *
   * @param loggerName  Name of the logger for internal identification.
   * @param logFileName Path and name of the log file for output.
   */
  private void createLog(String loggerName, String logFileName) {
    this.logger = java.util.logging.Logger.getLogger(loggerName);
    try {
      // Initializes FileHandler to write to the specified file, appending messages.
      this.fileHandler = new FileHandler(logFileName, true);

      // Sets a custom formatter to prepend a millisecond-precision timestamp to log messages.
      this.fileHandler.setFormatter(new SimpleFormatter() {
        @Override
        public String format(LogRecord record) {
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Logger.format);
          return simpleDateFormat.format(System.currentTimeMillis()) + " - " + record.getMessage() + "\n";
        }
      });

      this.logger.addHandler(this.fileHandler);
    } catch (IOException e) {
      // Fallback error handling prints initialization errors to standard error stream.
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Logger.format);
      System.err.println(simpleDateFormat.format(System.currentTimeMillis()) + " - " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Logs a message at INFO level.
   * Messages logged through this method are automatically formatted to include a timestamp.
   *
   * @param msg The message string to log.
   */
  @Override
  public void log(String msg) {
    this.logger.info(msg);
  }

  /**
   * Closes the FileHandler to properly release resources and flush any buffered log entries to the file.
   * Should be called before the Logger instance is discarded to ensure all messages are written to the log file.
   */
  @Override
  public void close() {
    this.fileHandler.close();
  }
}
