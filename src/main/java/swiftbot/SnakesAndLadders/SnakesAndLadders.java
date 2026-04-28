package swiftbot.SnakesAndLadders;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import swiftbot.Button;
import swiftbot.SwiftBotAPI;

public class SnakesAndLadders {

    static int[][] snakeData = {
            {17, 7},
            {12, 4}
    };

    static int[][] ladderData = {
            {3, 14},
            {8, 19}
    };

    public void start() {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;
        AtomicReference<Boolean> gameStarted = new AtomicReference<>(false);

        printBanner("SNAKES AND LADDERS");
        System.out.println("  Welcome! Press button [Y] on the SwiftBot to begin.");
        sleep(1);

        api.enableButton(Button.Y, () -> {
            System.out.println("\n  [Y] pressed -- Starting the game!");
            gameStarted.set(true);

            for (int i = 1; i <= 3; i++) {
                int[] green = {0, 255, 0};
                api.fillUnderlights(green);
                sleep(0.1);
                api.disableUnderlights();
                sleep(0.1);
            }
        });

        AtomicBoolean playerTurn = new AtomicBoolean(true);

        while (true) {
            if (!gameStarted.get()) {
                sleep(0.1);
                continue;
            }

            Scanner userInput = new Scanner(System.in);

            printSection("PLAYER REGISTRATION");
            System.out.print("  Enter your username: ");
            String username = userInput.nextLine().trim();

            if (username.isEmpty()) {
                username = "Player";
            }

            System.out.println("\n  Welcome, " + username + "! Let the game begin.\n");

            String swiftbotName = "Robot";

            String[][] board = {
                    {" 01 ", " 02 ", " 03 ", " 04 ", " 05 "},
                    {" 10 ", " 09 ", " 08 ", " 07 ", " 06 "},
                    {" 11 ", " 12 ", " 13 ", " 14 ", " 15 "},
                    {" 20 ", " 19 ", " 18 ", " 17 ", " 16 "},
                    {" 21 ", " 22 ", " 23 ", " 24 ", " 25 "}
            };

            printSection("BOARD SETUP");
            System.out.println("  Snakes:");
            for (int[] snake : snakeData) {
                System.out.println("    Head: sq " + snake[0] + " --> Tail: sq " + snake[1]);
            }

            System.out.println("  Ladders:");
            for (int[] ladder : ladderData) {
                System.out.println("    Bottom: sq " + ladder[0] + " --> Top: sq " + ladder[1]);
            }

            printSection("MODE SELECTION");
            System.out.println("  A. Normal Snakes and Ladders");
            System.out.println("  B. Override mode");
            System.out.print("\n  Your choice: ");
            String modeSelection = userInput.nextLine().trim().toLowerCase();

            boolean overrideMode = modeSelection.equals("b") || modeSelection.equals("mode b");

            if (overrideMode) {
                printBanner("MODE B -- OVERRIDE MODE");
            } else {
                printBanner("MODE A -- STANDARD GAME");
            }

            System.out.println("  You (" + username + ") vs " + swiftbotName);
            System.out.println("  First to reach square 25 wins!\n");

            System.out.println("  THE BOARD:");
            for (String[] row : board) {
                System.out.print("  ");
                for (String square : row) {
                    System.out.print("|" + square + "|");
                }
                System.out.println();
            }

            printSection("ROLLING FOR FIRST TURN");

            int playerDie;
            int swiftDie;

            do {
                playerDie = rollDie();
                swiftDie = rollDie();

                System.out.println("  " + username + " rolled: " + playerDie);
                System.out.println("  " + swiftbotName + " rolled: " + swiftDie);

                if (playerDie == swiftDie) {
                    System.out.println("  Tie! Rolling again...\n");
                    sleep(1);
                }
            } while (playerDie == swiftDie);

            playerTurn.set(playerDie > swiftDie);

            if (playerTurn.get()) {
                System.out.println("  --> " + username + " goes first!\n");
            } else {
                System.out.println("  --> " + swiftbotName + " goes first!\n");
            }

            int playerPosition = 1;
            int swiftPosition = 1;
            int robotCurrentPosition = 1;

            while (playerPosition < 25 && swiftPosition < 25) {
                printScore(username, playerPosition, swiftbotName, swiftPosition);

                if (playerTurn.get()) {
                    printSection(username.toUpperCase() + "'S TURN");

                    api.disableAllButtons();
                    System.out.println("  Press [A] on the SwiftBot to roll the die...");

                    AtomicBoolean dieRolled = new AtomicBoolean(false);
                    api.enableButton(Button.A, () -> dieRolled.set(true));

                    while (!dieRolled.get()) {
                        sleep(0.1);
                    }

                    api.disableAllButtons();

                    int roll = rollDie();
                    System.out.println("  Die rolled: " + roll);

                    int oldPlayerPosition = playerPosition;
                    playerPosition = applyMove(playerPosition, roll);
                    playerPosition = applySnakesAndLadders(playerPosition, username);

                    System.out.println("  " + username + " moves: sq " + oldPlayerPosition + " --> sq " + playerPosition);

                    if (checkSquareFive(api, username, playerPosition, swiftbotName, swiftPosition)) {
                        return;
                    }

                    System.out.println("\n  Move your piece to square " + playerPosition + ".");
                    System.out.println("  Lights are on -- press [A] once you have moved.");

                    int[] blue = {0, 0, 255};
                    api.fillUnderlights(blue);

                    AtomicBoolean turnDone = new AtomicBoolean(false);
                    api.enableButton(Button.A, () -> turnDone.set(true));

                    while (!turnDone.get()) {
                        sleep(0.1);
                    }

                    api.disableUnderlights();
                    api.disableAllButtons();

                    System.out.println("  Move confirmed! Passing to " + swiftbotName + "...\n");

                    playerTurn.set(false);

                } else {
                    printSection(swiftbotName.toUpperCase() + "'S TURN");

                    int roll = rollDie();
                    System.out.println("  " + swiftbotName + " rolls: " + roll);

                    int oldSwiftPosition = swiftPosition;

                    if (overrideMode && swiftPosition != 1) {
                        int chance = (int) (Math.random() * 101);
                        System.out.println("  Wheel of Fortune spins... (" + chance + ")");

                        if (chance % 2 == 0) {
                            printSection("OVERRIDE ACTIVATED");
                            System.out.print("  How many squares should the robot move? (1-5): ");

                            int overrideSquares = getNumberBetween(userInput, 1, 5);

                            System.out.print("  Forwards or Backwards? ");
                            String direction = userInput.nextLine().trim().toLowerCase();

                            if (direction.equals("forwards") || direction.equals("forward")) {
                                swiftPosition = applyMove(swiftPosition, overrideSquares);
                            } else if (direction.equals("backwards") || direction.equals("backward")) {
                                swiftPosition = Math.max(1, swiftPosition - overrideSquares);
                            } else {
                                System.out.println("  Invalid direction -- Robot rolls normally.");
                                swiftPosition = applyMove(swiftPosition, roll);
                            }
                        } else {
                            System.out.println("  No override -- Robot rolls normally.");
                            swiftPosition = applyMove(swiftPosition, roll);
                        }
                    } else {
                        swiftPosition = applyMove(swiftPosition, roll);
                    }

                    swiftPosition = applySnakesAndLadders(swiftPosition, swiftbotName);

                    System.out.println("  " + swiftbotName + " moves: sq " + oldSwiftPosition + " --> sq " + swiftPosition);

                    robotCurrentPosition = moveRobotToSquare(api, robotCurrentPosition, swiftPosition);

                    if (checkSquareFive(api, username, playerPosition, swiftbotName, swiftPosition)) {
                        return;
                    }

                    playerTurn.set(true);
                }

                if (playerPosition == 25) {
                    writeLog(username + " won the game!", username, playerPosition, swiftbotName, swiftPosition);
                    printBanner("*** " + username.toUpperCase() + " WINS! ***");
                    System.out.println("  Congratulations " + username + "! You reached square 25!");
                    return;
                }

                if (swiftPosition == 25) {
                    writeLog(swiftbotName + " won the game!", username, playerPosition, swiftbotName, swiftPosition);
                    printBanner("*** " + swiftbotName.toUpperCase() + " WINS! ***");
                    System.out.println("  Better luck next time, " + username + "!");
                    return;
                }
            }
        }
    }

    static int rollDie() {
        return (int) (Math.random() * 6) + 1;
    }

    static int getNumberBetween(Scanner scanner, int min, int max) {
        int number = -1;

        while (number < min || number > max) {
            try {
                number = Integer.parseInt(scanner.nextLine().trim());

                if (number < min || number > max) {
                    System.out.print("  Invalid! Enter " + min + "-" + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("  Not a number! Enter " + min + "-" + max + ": ");
            }
        }

        return number;
    }

    static boolean checkSquareFive(SwiftBotAPI api, String playerName, int playerPos,
                                   String botName, int botPos) {
        if (playerPos != 5 && botPos != 5) {
            return false;
        }

        String reason;

        if (playerPos == 5) {
            reason = "Checkpoint -- square 5 reached by " + playerName;
        } else {
            reason = "Checkpoint -- square 5 reached by " + botName;
        }

        writeLog(reason, playerName, playerPos, botName, botPos);

        printSection("CHECKPOINT -- SQUARE 5");
        System.out.println("  Someone landed on square 5!");
        System.out.println("  Press [B] to quit the game.");
        System.out.println("  Press [A] to keep playing.");

        AtomicBoolean keepPlaying = new AtomicBoolean(false);
        AtomicBoolean quitting = new AtomicBoolean(false);

        api.disableAllButtons();
        api.enableButton(Button.A, () -> keepPlaying.set(true));
        api.enableButton(Button.B, () -> quitting.set(true));

        while (!keepPlaying.get() && !quitting.get()) {
            sleep(0.1);
        }

        api.disableAllButtons();

        if (quitting.get()) {
            printSection("GAME ENDED BY PLAYER");
            System.out.println("  Thanks for playing!");
            return true;
        }

        System.out.println("  Continuing the game!\n");
        return false;
    }

    static void writeLog(String reason, String playerName, int playerPos,
                         String botName, int botPos) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH.mm.ss"));
        String filename = "snakes_log_" + date + "_" + time + ".txt";

        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.write("================================================\n");
            fw.write("  Snakes and Ladders -- Game Log\n");
            fw.write("================================================\n");
            fw.write("  Reason    : " + reason + "\n");
            fw.write("  Date      : " + date + "\n");
            fw.write("  Time      : " + time + "\n");
            fw.write("  " + playerName + " position : square " + playerPos + "\n");
            fw.write("  " + botName + " position : square " + botPos + "\n");
            fw.write("================================================\n\n");

            System.out.println("  [Log saved to: " + filename + "]");
        } catch (IOException e) {
            System.out.println("  [Could not save log: " + e.getMessage() + "]");
        }
    }

    static void flashSnake() {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;
        int[] red = {255, 0, 0};

        for (int i = 0; i < 3; i++) {
            api.fillUnderlights(red);
            sleep(0.4);
            api.disableUnderlights();
            sleep(0.3);
        }
    }

    static void flashLadder() {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;
        int[] yellow = {255, 200, 0};

        for (int i = 0; i < 3; i++) {
            api.fillUnderlights(yellow);
            sleep(0.4);
            api.disableUnderlights();
            sleep(0.3);
        }
    }

    static void printBanner(String text) {
        String border = "=".repeat(text.length() + 4);
        System.out.println("\n" + border);
        System.out.println("| " + text + " |");
        System.out.println(border + "\n");
    }

    static void printSection(String text) {
        System.out.println("\n>> " + text);
        System.out.println("-".repeat(text.length() + 3));
    }

    static void printScore(String playerName, int playerPos, String botName, int botPos) {
        System.out.println("\n+-----------------------+");
        System.out.println("|  SCOREBOARD           |");
        System.out.printf("|  %-10s  sq: %2d   |%n", playerName, playerPos);
        System.out.printf("|  %-10s  sq: %2d   |%n", botName, botPos);
        System.out.println("+-----------------------+\n");
    }

    static int applyMove(int currentPos, int roll) {
        int spacesLeft = 25 - currentPos;

        if (roll > spacesLeft) {
            System.out.println("  Cannot move -- would overshoot 25! Staying on square " + currentPos + ".");
            return currentPos;
        }

        return currentPos + roll;
    }

    static int applySnakesAndLadders(int position, String playerName) {
        for (int[] snake : snakeData) {
            if (snake[0] == position) {
                flashSnake();
                System.out.println("  SNAKE! " + playerName + " lands on sq " + snake[0]
                        + " and slides down to sq " + snake[1] + "!");
                return snake[1];
            }
        }

        for (int[] ladder : ladderData) {
            if (ladder[0] == position) {
                flashLadder();
                System.out.println("  LADDER! " + playerName + " lands on sq " + ladder[0]
                        + " and climbs up to sq " + ladder[1] + "!");
                return ladder[1];
            }
        }

        return position;
    }

    static int moveRobotToSquare(SwiftBotAPI api, int startPos, int targetSquare) {
        int currentPos = startPos;

        System.out.println("  [Robot] Navigating from sq " + currentPos + " to sq " + targetSquare);

        while (currentPos != targetSquare) {
            int nextPos = currentPos + 1;

            if (isRowEndSquare(currentPos)) {
                if (isEvenRowEnd(currentPos)) {
                    makeLeftCorner();
                } else {
                    makeRightTurn();
                }
            } else {
                System.out.println("  [Robot] Moving straight -> sq " + nextPos);
                api.move(80, 100, 897);
                sleep(1);
            }

            currentPos = nextPos;
        }

        api.stopMove();
        System.out.println("  [Robot] Arrived at square " + targetSquare);

        return currentPos;
    }

    static boolean isRowEndSquare(int square) {
        return square == 5 || square == 10 || square == 15 || square == 20;
    }

    static boolean isEvenRowEnd(int square) {
        return square == 5 || square == 15;
    }

    static void makeLeftCorner() {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;

        System.out.println("  [Robot] Turning left");
        api.move(0, 100, 725);
        sleep(1);

        api.move(80, 100, 465);
        sleep(1);

        api.move(0, 100, 850);
        sleep(1);

        System.out.println("  [Robot] Left turn complete");
    }

    static void makeRightTurn() {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;

        System.out.println("  [Robot] Turning right");
        api.move(90, 0, 725);
        sleep(1);

        api.move(80, 100, 495);
        sleep(1);

        api.move(90, 0, 850);
        sleep(1);

        System.out.println("  [Robot] Right turn complete");
    }

    static double sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
            return seconds;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}