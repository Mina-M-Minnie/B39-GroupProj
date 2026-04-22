package swiftbot.SpyBot;

import java.util.Scanner;

import swiftbot.SwiftBotAPI;

public class NavigateToDestination {

    private final SwiftBotAPI swiftBot;
    private final Scanner input;

    // Movement calibration constants
    // These values will need tuning on your physical SwiftBot
    // SIDE_TRAVEL_MS = time in milliseconds to travel 50cm at the given speed
    // TURN_MS        = time in milliseconds to turn 120 degrees (equilateral triangle corner)
    private static final int MOTOR_SPEED    = 50;
    private static final int SIDE_TRAVEL_MS = 3000; // tune this: time to travel 50cm
    private static final int TURN_MS        = 800;  // tune this: time to turn 120 degrees

    public NavigateToDestination(SwiftBotAPI swiftBot, Scanner input) {
        this.swiftBot = swiftBot;
        this.input    = input;
    }

    /**
     * Navigates the SwiftBot from its current location to the destination.
     * The three safe houses A, B, C sit at the corners of an equilateral triangle.
     * The bot must follow the triangle edges only — no shortcuts.
     *
     * Valid direct edges: A-B, B-A, B-C, C-B, A-C, C-A (all one hop)
     * All routes are one edge (50cm) since it is equilateral.
     *
     * @param from         current location e.g. "A"
     * @param destination  destination location e.g. "B"
     * @param priority     message priority (affects LED colour on arrival)
     */
    public void navigate(String from, String destination, PrioritySelection.Priority priority) {

        // No movement needed if already at destination
        if (from.equals(destination)) {
            System.out.println("Already at destination " + destination + ". No movement needed.");
            return;
        }

        boolean navigationComplete = false;

        while (!navigationComplete) {

            try {
                // Navigation Status Screen
                System.out.println(
                    "----------------------- NAVIGATION -----------------------------\n" +
                    "Step 6/7: Travelling to destination safe house.\n" +
                    "\n" +
                    "From: " + from + "    To: " + destination + "\n" +
                    "Track: Equilateral triangle (50 cm per side)\n" +
                    "Movement mode: Track-only (no shortcuts)\n" +
                    "\n" +
                    "Orientation: Facing " + from + destination + "\n" +
                    "Motors: ACTIVE\n" +
                    "LEDs: OFF (during travel)\n" +
                    "\n" +
                    "Progress:\n" +
                    "- Departed " + from + " ✅"
                );

                // Turn off LEDs during travel
                swiftBot.disableUnderlights();

                // Execute the movement along the triangle edge
                travelEdge(from, destination);

                System.out.println("- En route to " + destination + " ...");
                System.out.println("- Arrived at " + destination + " ✅");

                navigationComplete = true;

                // Arrival at Destination Screen
                // Flash LEDs based on priority to signal a message is waiting
                String ledDescription = getArrivalLEDDescription(priority);
                flashArrivalLEDs(priority);

                System.out.println(
                    "\n✅ ARRIVED at safe house: " + destination + "\n" +
                    "\n" +
                    "Message waiting indicator:\n" +
                    "Priority: " + priority + "\n" +
                    "SwiftBot LEDs: " + ledDescription + "\n" +
                    "\n" +
                    "Receiver verification required.\n" +
                    "Press ENTER to begin receiver authentication...\n" +
                    "> "
                );
                input.nextLine();
                System.out.println("-----------------------------------------------------------------");

            } catch (InterruptedException e) {

                // UI-07B: Navigation Interruption
                swiftBot.stopMove();
                System.out.println(
                    "\n❌ WARNING: Navigation interrupted.\n" +
                    "Possible causes:\n" +
                    "- Wheel slip / obstruction\n" +
                    "- Motor command failed\n" +
                    "- Unexpected orientation state\n" +
                    "\n" +
                    "Action:\n" +
                    "1) Retry navigation to " + destination + "\n" +
                    "2) Abort and return to sender\n" +
                    "\n" +
                    "Choose (1/2):\n" +
                    "> "
                );

                String choice;
                do {
                    choice = input.nextLine().trim();
                } while (!choice.equals("1") && !choice.equals("2"));

                if (choice.equals("2")) {
                    // Abort — navigate back to sender
                    System.out.println("Aborting. Returning to sender at " + from + "...");
                    try {
                        travelEdge(destination, from);
                    } catch (InterruptedException ex) {
                        swiftBot.stopMove();
                    }
                    return;
                }
                // choice 1 = retry, loop continues
                System.out.println("Retrying navigation to " + destination + "...");
            }
        }
    }

    /**
     * Moves the SwiftBot along one edge of the triangle from one corner to another.
     * All edges are 50cm. The turn direction depends on which edge is being traversed.
     *
     * Triangle corners (equilateral, corners labelled):
     *
     * Turn angles at each corner are 120 degrees (exterior angle of equilateral triangle).
     * Turn left or right depending on the direction of travel.
     */
    private void travelEdge(String from, String to) throws InterruptedException {

        // Determine turn direction based on which edge we are travelling
        // Facing the next corner requires a specific turn at departure
        boolean turnLeft = shouldTurnLeft(from, to);

        // Turn to face the destination corner
        if (turnLeft) {
            swiftBot.move(-MOTOR_SPEED, MOTOR_SPEED, TURN_MS); // turn left
        } else {
            swiftBot.move(MOTOR_SPEED, -MOTOR_SPEED, TURN_MS); // turn right
        }

        // Travel the 50cm side
        swiftBot.move(MOTOR_SPEED, MOTOR_SPEED, SIDE_TRAVEL_MS);

        // Stop at destination
        swiftBot.stopMove();
    }

    /**
     * Determines whether the SwiftBot should turn left or right to face the
     * destination corner, based on the equilateral triangle layout:
     *
     * Starting orientation assumption: bot always faces the next node clockwise.
     */
    private boolean shouldTurnLeft(String from, String to) {
        // Clockwise: A -> C -> B -> A  = turn right
        // Counter-clockwise: A -> B -> C -> A = turn left
        if (from.equals("A") && to.equals("B")) return true;
        if (from.equals("B") && to.equals("C")) return true;
        if (from.equals("C") && to.equals("A")) return true;
        return false; // right turn for A->C, C->B, B->A
    }

    /**
     * Flashes the underlights on arrival based on message priority.
     * URGENT = fast red, NORMAL = steady yellow, LOW = slow white
     */
    private void flashArrivalLEDs(PrioritySelection.Priority priority) throws InterruptedException {
        int[] colour;
        int onTime;
        int offTime;
        int flashes = 6; // number of blinks

        switch (priority) {
            case URGENT:
                colour  = new int[]{255, 0, 0};   // red
                onTime  = 150;                      // fast
                offTime = 150;
                break;
            case NORMAL:
                colour  = new int[]{255, 200, 0};  // yellow
                onTime  = 500;                      // steady
                offTime = 500;
                break;
            case LOW:
            default:
                colour  = new int[]{255, 255, 255}; // white
                onTime  = 800;                       // slow
                offTime = 800;
                break;
        }

        // Flash the LEDs the specified number of times
        for (int i = 0; i < flashes; i++) {
            swiftBot.fillUnderlights(colour);
            Thread.sleep(onTime);
            swiftBot.disableUnderlights();
            Thread.sleep(offTime);
        }

        // Leave lights on after flashing to show still waiting
        swiftBot.fillUnderlights(colour);
    }

    /**
     * Returns a human readable description of the arrival LED behaviour
     * for the UI-07A screen.
     */
    private String getArrivalLEDDescription(PrioritySelection.Priority priority) {
        switch (priority) {
            case URGENT: return "FLASHING RED (fast)";
            case NORMAL: return "FLASHING YELLOW (steady)";
            case LOW:    return "FLASHING WHITE (slow)";
            default:     return "FLASHING RED (fast)";
        }
    }
}