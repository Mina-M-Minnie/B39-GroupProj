package SpyBot;
import java.util.Scanner;

public class PrioritySelection {

    // The three valid priority levels
    public enum Priority { URGENT, NORMAL, LOW }

    private final ButtonInputHandler buttonHandler;
    private final Scanner input;

    public PrioritySelection(ButtonInputHandler buttonHandler, Scanner input) {
        this.buttonHandler = buttonHandler;
        this.input         = input;
    }

    /**
     * Displays the priority selection screen and waits for a valid button press.
     * X = URGENT, Y = NORMAL, A = LOW
     * B is invalid and shows an error screen.
     *
     * Returns the selected Priority level.
     */
    public Priority selectPriority() {

        Priority selectedPriority = null;

        while (selectedPriority == null) {

            // Priority Selection Screen
            System.out.println(
                "-------------------- PRIORITY LEVEL ----------------------------\n" +
                "Step 5/7: Select message priority (SwiftBot buttons)\n" +
                "\n" +
                "X = URGENT\n" +
                "Y = NORMAL\n" +
                "A = LOW\n" +
                "\n" +
                "Waiting for priority input...\n" +
                "Status: AWAITING_PRIORITY\n" +
                "-----------------------------------------------------------------"
            );

            // Wait for a button press
            MorseCodeInputs press = buttonHandler.waitForPress();

            switch (press) {

                case DOT:
                    // X button = URGENT
                    selectedPriority = Priority.URGENT;
                    break;

                case DASH:
                    // Y button = NORMAL
                    selectedPriority = Priority.NORMAL;
                    break;

                case END_OF_CHAR:
                    // A button = LOW
                    selectedPriority = Priority.LOW;
                    break;

                case END_OF_WORD:
                    // B button = invalid for priority selection
                    System.out.println(
                        "❌ ERROR: Invalid button for priority.\n" +
                        "Use X (Urgent), Y (Normal), or A (Low).\n" +
                        "\n" +
                        "Try again..."
                    );
                    // Loop back to show the priority screen again
                    break;
            }
        }

        // Priority Confirmed Screen
        System.out.println(
            "\n" +
            "Priority selected: " + selectedPriority + "\n" +
            "Arrival indicator behaviour:\n" +
            "- URGENT: Flash RED (fast)\n" +
            "- NORMAL: Flash YELLOW (steady)\n" +
            "- LOW:    Flash WHITE (slow)\n" +
            "\n" +
            "Press ENTER to begin navigation...\n" +
            "> "
        );
        input.nextLine();
        System.out.println("-----------------------------------------------------------------");

        return selectedPriority;
    }
}