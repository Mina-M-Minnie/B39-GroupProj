package swiftbot.SpyBot;

import java.util.Scanner;

public class RecordMorseMessage {

    // Dependencies injected via constructor
    private final ButtonInputHandler buttonHandler;
    private final MorseCodeTranslator translator;
    private final Scanner input;

    public RecordMorseMessage(ButtonInputHandler buttonHandler, Scanner input) {
        this.buttonHandler = buttonHandler;
        this.translator    = new MorseCodeTranslator();
        this.input         = input;
    }

    public String recordMorseMessage() {

        // Holds the final raw morse string to return
        String encodedMessage = null;

        // Keeps looping until a valid correctly formatted message is recorded
        boolean validMessage = false;

        while (!validMessage) {

            // Live Message Recording Screen
            System.out.println(
                "------------------- MESSAGE RECORDING (LIVE) -------------------\n" +
                "Step 3/7: Recording input from SwiftBot buttons...\n" +
                "\n" +
                "Live stream (most recent at end):\n" +
                "Morse: [ ]\n" +
                "\n" +
                "Legend: . = DOT    - = DASH    | = end-char    / = end-word\n" +
                "\n" +
                "Waiting for button presses...\n" +
                "Status: RECORDING\n" +
                "-----------------------------------------------------------------"
            );

            // --- StringBuilders ---
            StringBuilder morseMessage  = new StringBuilder(); // raw morse to return  e.g. ".-|-..|/"
            StringBuilder currentSymbol = new StringBuilder(); // dots/dashes being entered right now e.g. ".-"
            StringBuilder currentWord   = new StringBuilder(); // decoded letters for the current word e.g. "WAN"
            StringBuilder fullMessage   = new StringBuilder(); // full decoded message e.g. "B WANT GEAR"
            StringBuilder liveStream    = new StringBuilder(); // visual display in terminal

            boolean messageComplete = false;

            while (!messageComplete) {

                MorseCodeInputs press = buttonHandler.waitForPress();

                switch (press) {

                    case DOT:
                        // Add dot to the current symbol buffer and update live display
                        currentSymbol.append(".");
                        liveStream.append(".");
                        System.out.println("Morse: [ " + liveStream + " ]");
                        break;

                    case DASH:
                        // Add dash to the current symbol buffer and update live display
                        currentSymbol.append("-");
                        liveStream.append("-");
                        System.out.println("Morse: [ " + liveStream + " ]");
                        break;

                    case END_OF_CHAR:
                        String symbol = currentSymbol.toString();

                        // "-----" is morse for 0 — used as the end of message signal
                        if (symbol.equals("-----")) {
                            morseMessage.append("-----");
                            liveStream.append("|");
                            System.out.println("Morse: [ " + liveStream + " ]");
                            System.out.println("Status: END OF MESSAGE RECEIVED");

                            // Flush any remaining decoded word into fullMessage
                            if (currentWord.length() > 0) {
                                if (fullMessage.length() > 0) fullMessage.append(" ");
                                fullMessage.append(currentWord);
                            }
                            messageComplete = true;
                            break;
                        }

                        // Reject unrecognised morse symbols
                        if (!translator.isValid(symbol)) {
                            System.out.println("Status: INVALID SYMBOL '" + symbol + "' -- re-enter character");
                            currentSymbol.setLength(0);
                            liveStream.append("?|");
                            break;
                        }

                        // Translate symbol to character and add to current word
                        String decodedChar = translator.translate(symbol);
                        currentWord.append(decodedChar);

                        // Append raw morse symbol to the morse output
                        morseMessage.append(symbol).append("|");
                        liveStream.append("|");
                        currentSymbol.setLength(0);
                        System.out.println("Morse: [ " + liveStream + " ]  -> '" + decodedChar + "'");
                        break;

                    case END_OF_WORD:
                        // Flush any pending symbol the user may have forgotten to END_OF_CHAR
                        if (currentSymbol.length() > 0 && translator.isValid(currentSymbol.toString())) {
                            currentWord.append(translator.translate(currentSymbol.toString()));
                            morseMessage.append(currentSymbol).append("|");
                            currentSymbol.setLength(0);
                        }

                        // Add completed word to the full decoded message
                        if (currentWord.length() > 0) {
                            if (fullMessage.length() > 0) fullMessage.append(" ");
                            fullMessage.append(currentWord);

                            // Append word separator to morse output
                            morseMessage.append("/");
                            liveStream.append("/");
                            System.out.println("Morse: [ " + liveStream + " ]  -> Word: '" + currentWord + "'");
                            currentWord.setLength(0);
                        }
                        break;
                }
            }

            // -------------------------------------------------------------------
            // Validate format: first word must be a single location (A, B or C)
            // followed by at least one more word as the message body
            // e.g. "B WANT GEAR" is valid, "WANT GEAR" is not
            // -------------------------------------------------------------------
            String result = fullMessage.toString().trim();
            String[] parts = result.split(" ", 2);

            boolean validDestination = parts.length == 2
                    && parts[0].length() == 1
                    && parts[0].matches("[ABC]")
                    && parts[1].length() > 0;

            if (validDestination) {
                // Message is valid — store and display UI-05 confirmation
                validMessage  = true;
                encodedMessage = morseMessage.toString();

                // Build encoded display of message payload for the confirmation screen
                String encodedDisplay = buildEncodedDisplay(parts[1]);

                // UI-05: Destination Extraction and Confirmation
                System.out.println(
                    "------------------ DESTINATION EXTRACTION ----------------------\n" +
                    "Step 4/7: Processing stored Morse...\n" +
                    "\n" +
                    "Extracting first word as destination...\n" +
                    "Destination (decoded): " + parts[0] + "\n" +
                    "Validating location... OK (A/B/C)\n" +
                    "\n" +
                    "Remaining message payload:\n" +
                    "Encoded:  [ " + encodedDisplay + " ]\n" +
                    "Decoded:  \"" + parts[1] + "\"\n" +
                    "\n" +
                    "Press ENTER to proceed to priority selection...\n" +
                    "> "
                );
                input.nextLine();
                System.out.println("-----------------------------------------------------------------");

            } else {
                // Format was wrong — show error and prompt to re-record
                System.out.println(
                    "\n-----------------------------------------------------------------\n" +
                    "Status: INVALID FORMAT\n" +
                    "❌ Message must start with a single destination (A, B or C)\n" +
                    "   followed by END OF WORD, then your message.\n" +
                    "   e.g.  B <end word> WANT <end word> GEAR <end message>\n" +
                    "   Recorded was: '" + result + "'\n" +
                    "-----------------------------------------------------------------"
                );

                // Ask user if they want to re-record
                String answer;
                do {
                    System.out.print("Re-record message? (Y/N)\n> ");
                    answer = input.nextLine().toUpperCase();
                } while (!answer.equals("Y") && !answer.equals("N"));

                // If user declines return null so SpyBot can handle shutdown
                if (answer.equals("N")) {
                    return null;
                }
                // If Y the outer while(!validMessage) loop repeats automatically
            }
        }

        return encodedMessage;
    }

    /**
     * Converts a decoded message string back into a morse encoded display string.
     * Used for the UI-05 confirmation screen only — not the returned value.
     * e.g. "WANT GEAR" -> ".-- | .- | -. | - / --. | . | .- | .-. / 0"
     *
     * @param message  decoded message payload e.g. "WANT GEAR"
     * @return         morse encoded display string
     */
    private String buildEncodedDisplay(String message) {

        // Reverse lookup map: character -> morse symbol
        java.util.Map<String, String> charToMorse = new java.util.HashMap<>();
        charToMorse.put("A",".-");   charToMorse.put("B","-...");
        charToMorse.put("C","-.-."); charToMorse.put("D","-..");
        charToMorse.put("E",".");    charToMorse.put("F","..-.");
        charToMorse.put("G","--.");  charToMorse.put("H","....");
        charToMorse.put("I","..");   charToMorse.put("J",".---");
        charToMorse.put("K","-.-");  charToMorse.put("L",".-..");
        charToMorse.put("M","--");   charToMorse.put("N","-.");
        charToMorse.put("O","---");  charToMorse.put("P",".--.");
        charToMorse.put("Q","--.-"); charToMorse.put("R",".-.");
        charToMorse.put("S","...");  charToMorse.put("T","-");
        charToMorse.put("U","..-");  charToMorse.put("V","...-");
        charToMorse.put("W",".--");  charToMorse.put("X","-..-");
        charToMorse.put("Y","-.--"); charToMorse.put("Z","--..");

        StringBuilder encoded = new StringBuilder();
        String[] words = message.split(" ");

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            for (int j = 0; j < word.length(); j++) {
                String ch = String.valueOf(word.charAt(j));
                encoded.append(charToMorse.getOrDefault(ch, "?"));

                // Separate characters within a word with |
                if (j < word.length() - 1) {
                    encoded.append(" | ");
                }
            }

            // Separate words with / and mark end of message with 0
            if (i < words.length - 1) {
                encoded.append(" / ");
            } else {
                encoded.append(" / 0");
            }
        }

        return encoded.toString();
    }
}