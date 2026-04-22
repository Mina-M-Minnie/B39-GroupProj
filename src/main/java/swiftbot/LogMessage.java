package swiftbot;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class LogMessage {

    private final Scanner input;

    // Log file path
    private static final String LOG_FILE_PATH = "spybot_messages.txt";

    // Date/time formatter for timestamps
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public LogMessage(Scanner input) {
        this.input = input;
    }

    /**
     * Writes the communication log entry to a text file and displays UI-10.
     *
     * @param senderCallsign    sender's callsign e.g. "EAGLE7"
     * @param senderLocation    sender's location e.g. "A"
     * @param receiverCallsign  receiver's callsign e.g. "VIPER3"
     * @param receiverLocation  receiver's location e.g. "B"
     * @param destination       destination location e.g. "B"
     * @param decodedMessage    plain text message e.g. "WANT GEAR"
     * @param priority          message priority level
     * @param timeRecorded      timestamp when message was recorded
     * @param timeDelivered     timestamp when message was delivered
     */
    public void log(
            String senderCallsign,
            String senderLocation,
            String receiverCallsign,
            String receiverLocation,
            String destination,
            String decodedMessage,
            PrioritySelection.Priority priority,
            LocalDateTime timeRecorded,
            LocalDateTime timeDelivered) {

        // Logging Screen
        System.out.println(
            "-------------------------- LOGGING ------------------------------\n" +
            "Writing communication log...\n" +
            "\n" +
            "Fields:\n" +
            "- Sender callsign + location\n" +
            "- Receiver callsign + location\n" +
            "- Destination\n" +
            "- Message (plain text)\n" +
            "- Time recorded\n" +
            "- Time delivered\n" +
            "- Priority"
        );

        // Build the log entry
        String logEntry = buildLogEntry(
            senderCallsign, senderLocation,
            receiverCallsign, receiverLocation,
            destination, decodedMessage,
            priority, timeRecorded, timeDelivered
        );

        // Write to file
        boolean success = writeToFile(logEntry);

        if (success) {
            System.out.println(
                "\n✅ Log saved successfully.\n" +
                "Log file path:\n" +
                LOG_FILE_PATH + "\n"
            );
        } else {
            System.out.println(
                "\n❌ ERROR: Failed to write log file.\n" +
                "Attempted path: " + LOG_FILE_PATH + "\n"
            );
        }

        System.out.println(
            "Press ENTER to start return countdown...\n" +
            "> "
        );
        input.nextLine();
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Builds the formatted log entry string to be written to the file.
     */
    private String buildLogEntry(
            String senderCallsign,
            String senderLocation,
            String receiverCallsign,
            String receiverLocation,
            String destination,
            String decodedMessage,
            PrioritySelection.Priority priority,
            LocalDateTime timeRecorded,
            LocalDateTime timeDelivered) {

        return  "=================================================================\n" +
                "SPYBOT MESSAGE LOG\n" +
                "=================================================================\n" +
                "Sender Callsign : " + senderCallsign  + "\n" +
                "Sender Location : " + senderLocation  + "\n" +
                "Receiver Callsign: " + receiverCallsign + "\n" +
                "Receiver Location: " + receiverLocation + "\n" +
                "Destination      : " + destination     + "\n" +
                "Message          : " + decodedMessage  + "\n" +
                "Priority         : " + priority        + "\n" +
                "Time Recorded    : " + timeRecorded.format(FORMATTER) + "\n" +
                "Time Delivered   : " + timeDelivered.format(FORMATTER) + "\n" +
                "=================================================================\n";
    }

    /**
     * Appends the log entry to the log file.
     * Uses append mode so previous logs are not overwritten.
     *
     * @param logEntry  the formatted log string to write
     * @return          true if successful, false if an error occurred
     */
    private boolean writeToFile(String logEntry) {
        // append=true means each new log is added to the end of the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.println(logEntry);
            return true;
        } catch (IOException e) {
            System.err.println("Log write error: " + e.getMessage());
            return false;
        }
    }
}