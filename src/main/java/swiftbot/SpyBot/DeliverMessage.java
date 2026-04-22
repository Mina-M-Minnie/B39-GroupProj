package swiftbot.SpyBot;

import java.util.Scanner;

import swiftbot.SwiftBotAPI;

public class DeliverMessage {

    private final SwiftBotAPI swiftBot;
    private final Scanner input;

    // LED colours for each morse symbol type
    private static final int[] WHITE  = {255, 255, 255}; // DOT
    private static final int[] BLUE   = {0,   0,   255}; // DASH
    private static final int[] AMBER  = {255, 140,  0};  // END OF CHAR
    private static final int[] RED    = {255,   0,  0};  // END OF WORD
    private static final int[] GREEN  = {0,   255,  0};  // END OF MESSAGE

    // Timing constants in milliseconds
    private static final int DOT_MS       = 600;  // white flash duration
    private static final int DASH_MS      = 1200;  // blue flash duration
    private static final int SIGNAL_MS    = 800;  // amber/red/green flash duration
    private static final int GAP_MS       = 400;  // gap between flashes

    public DeliverMessage(SwiftBotAPI swiftBot, Scanner input) {
        this.swiftBot = swiftBot;
        this.input    = input;
    }

    /**
     * Delivers the morse message via SwiftBot underlights.
     * First transmits the sender callsign, then the message payload.
     *
     * LED colour mapping:
     *   DOT      -> WHITE
     *   DASH     -> BLUE
     *   END CHAR -> AMBER
     *   END WORD -> RED
     *   END MSG  -> GREEN
     *
     * @param morseMessage    the raw morse string e.g. "-...|/ .--|.-|-.|-----"
     * @param senderCallsign  the sender's callsign e.g. "EAGLE7"
     * @param senderLocation  the sender's location e.g. "A"
     */
    
    public void deliver(String morseMessage, String senderCallsign, String senderLocation) throws InterruptedException {

        // Decode the message payload for display (everything after the first /)
        String[] splitOnWord = morseMessage.split("/", 2);
        String destinationMorse = splitOnWord[0];
        String payloadMorse     = splitOnWord.length > 1 ? splitOnWord[1].trim() : "";

        // Decode payload for the terminal display
        String decodedPayload = decodeMorseForDisplay(payloadMorse);

        // Build sender callsign in morse for transmission
        String senderMorse = buildCallsignMorse(senderCallsign);

        //Message Delivery Screen
        System.out.println(
            "--------------------- MESSAGE DELIVERY -------------------------\n" +
            "Delivering message via underlights (Morse colours):\n" +
            "\n" +
            "DOT  (.)  -> WHITE\n" +
            "DASH (-)  -> BLUE\n" +
            "END CHAR  -> AMBER\n" +
            "END WORD  -> RED\n" +
            "END MSG   -> GREEN\n" +
            "\n" +
            "Delivery format:\n" +
            "<SENDER> <end word> <MESSAGE> <end message>\n" +
            "\n" +
            "Now delivering:\n" +
            "Sender:  " + senderCallsign + "\n" +
            "Message: " + decodedPayload + "\n" +
            "\n" +
            "Status: PLAYING_LED_SEQUENCE\n" +
            "-----------------------------------------------------------------"
        );

        // --- Transmit sender callsign first ---
        System.out.println("[Transmitting sender identity: " + senderCallsign + "]");
        playMorseString(senderMorse);

        // End of sender word = RED flash
        flashLED(RED, SIGNAL_MS);
        Thread.sleep(GAP_MS);

        //Transmit message payload
        System.out.println("[Transmitting message payload...]");
        playMorseString(payloadMorse);

        // Final GREEN flash = end of message
        flashLED(GREEN, SIGNAL_MS * 2);
        swiftBot.disableUnderlights();

        //Delivery Complete Screen
        System.out.println(
            "\n✅ DELIVERY COMPLETE\n" +
            "SwiftBot LEDs: GREEN (end-of-message)\n" +
            "\n" +
            "Press ENTER to continue...\n" +
            "> "
        );
        input.nextLine();
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Plays a morse string through the SwiftBot underlights.
     * Parses the raw morse format: ".-|--.|/" where | = end char, / = end word
     *
     * @param morseString  raw morse e.g. ".-|-.|/"
     */
    private void playMorseString(String morseString) throws InterruptedException {

        if (morseString == null || morseString.isEmpty()) return;

        // Split into characters by | (end of char marker)
        // Split into words by / (end of word marker)
        String[] words = morseString.split("/");

        for (int w = 0; w < words.length; w++) {
            String word = words[w].trim();
            if (word.isEmpty()) continue;

            // Split word into individual morse symbols by |
            String[] symbols = word.split("\\|");

            for (String symbol : symbols) {
                symbol = symbol.trim();
                if (symbol.isEmpty()) continue;

                // Skip end of message signal
                if (symbol.equals("-----") && w == words.length - 1) break;

                // Play each dot or dash in the symbol
                for (char c : symbol.toCharArray()) {
                    if (c == '.') {
                        flashLED(WHITE, DOT_MS);
                    } else if (c == '-') {
                        flashLED(BLUE, DASH_MS);
                    }
                    Thread.sleep(GAP_MS);
                }

                // AMBER flash = end of character
                flashLED(AMBER, SIGNAL_MS);
                Thread.sleep(GAP_MS);
            }

            // RED flash = end of word (not after last word)
            if (w < words.length - 1) {
                flashLED(RED, SIGNAL_MS);
                Thread.sleep(GAP_MS);
            }
        }
    }

    /**
     * Flashes all underlights a single colour for a given duration then turns off.
     *
     * @param colour     RGB array e.g. {255, 255, 255}
     * @param durationMs how long to hold the colour in milliseconds
     */
    
    private void flashLED(int[] colour, int durationMs) throws InterruptedException {
        swiftBot.fillUnderlights(colour);
        Thread.sleep(durationMs);
        swiftBot.disableUnderlights();
    }

    /**
     * Converts a callsign string into raw morse format for transmission.
     * e.g. "EAGLE7" -> ".|.-|--.|.-..|."
     */
    
    private String buildCallsignMorse(String callsign) {
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
        charToMorse.put("0","-----"); charToMorse.put("1",".----");
        charToMorse.put("2","..---"); charToMorse.put("3","...--");
        charToMorse.put("4","....-"); charToMorse.put("5",".....");
        charToMorse.put("6","-...."); charToMorse.put("7","--...");
        charToMorse.put("8","---."); charToMorse.put("9","----.");

        StringBuilder morse = new StringBuilder();
        for (char c : callsign.toUpperCase().toCharArray()) {
            String symbol = charToMorse.getOrDefault(String.valueOf(c), "");
            if (!symbol.isEmpty()) {
                if (morse.length() > 0) morse.append("|");
                morse.append(symbol);
            }
        }
        return morse.toString();
    }

    /**
     * Decodes a raw morse string back to plain text for terminal display only.
     * e.g. ".--|.-|-.|/" -> "WAN"
     */
    
    private String decodeMorseForDisplay(String morseString) {
        MorseCodeTranslator translator = new MorseCodeTranslator();
        StringBuilder decoded = new StringBuilder();
        String[] words = morseString.split("/");

        for (int w = 0; w < words.length; w++) {
            String word = words[w].trim();
            if (word.isEmpty()) continue;

            String[] symbols = word.split("\\|");
            for (String symbol : symbols) {
                symbol = symbol.trim();
                if (symbol.equals("-----")) continue; // skip end of message
                if (translator.isValid(symbol)) {
                    decoded.append(translator.translate(symbol));
                }
            }

            if (w < words.length - 1) decoded.append(" ");
        }

        return decoded.toString().trim();
    }
}