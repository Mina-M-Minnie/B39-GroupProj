package swiftbot.SnakesAndLadders;

import swiftbot.Button;
import swiftbot.SwiftBotAPI;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SnakesAndLadders {


    // ── Snake data: [head, tail] ─────────────────────────────────────────────
    static int[][] snakeData = {
            {17, 7},
            {12, 4}
    };

    // ── Ladder data: [bottom, top] ───────────────────────────────────────────
    static int[][] ladderData = {
            {3, 14},
            {8, 19}
    };

    static void writeLog(String reason, String playerName, int playerPos,
                         String botName, int botPos) {
        String date     = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String time     = LocalTime.now().format(DateTimeFormatter.ofPattern("HH.mm.ss"));
        String filename = "snakes_log_" + date + "_" + time + ".txt";

        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.write("================================================\n");
            fw.write("  Snakes and Ladders -- Game Log\n");
            fw.write("================================================\n");
            fw.write("  Reason    : " + reason      + "\n");
            fw.write("  Date      : " + date         + "\n");
            fw.write("  Time      : " + time         + "\n");
            fw.write("  " + playerName + " position : square " + playerPos + "\n");
            fw.write("  " + botName    + " position : square " + botPos    + "\n");
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
        System.out.printf ("|  %-10s  sq: %2d   |%n", playerName, playerPos);
        System.out.printf ("|  %-10s  sq: %2d   |%n", botName,    botPos);
        System.out.println("+-----------------------+\n");
    }

    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;
        AtomicReference<Boolean> gameStarted = new AtomicReference<>(false);

        printBanner("SNAKES AND LADDERS");
        System.out.println("  Welcome! Press button [Y] on the SwiftBot to begin.");
        sleep(1);

        // ── Game start button (original lines 34-44 pattern kept intact) ────
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
            if (!gameStarted.get()) continue;

            // ── Player name ──────────────────────────────────────────────────
            Scanner userimput = new Scanner(System.in);
            printSection("PLAYER REGISTRATION");
            sleep(1);
            System.out.print("  Enter your username: ");
            String username = userimput.nextLine().trim();
            sleep(1);
            System.out.println("\n  Welcome, " + username + "! Let the game begin.\n");
            sleep(1);

            String swiftbotname = "Robot";

            String[][] SnakesAndLaddersGameBoard =
                    {{" 01 ", " 02 ", " 03 ", " 04 ", " 05 "},
                            {" 10 ", " 09 ", " 08 ", " 07 ", " 06 "},
                            {" 11 ", " 12 ", " 13 ", " 14 ", " 15 "},
                            {" 20 ", " 19 ", " 18 ", " 17 ", " 16 "},
                            {" 21 ", " 22 ", " 23 ", " 24 ", " 25 "}};

            String[] wheeloffortune = {"Override Spin", "Unfortunately not"};

            // ── Board setup display ──────────────────────────────────────────
            printSection("BOARD SETUP");
            sleep(1);
            System.out.println("  Snakes (watch out!):");
            for (int[] snake : snakeData) {
                System.out.println("    [!] Head: sq " + snake[0] + "  -->  Tail: sq " + snake[1]);
                sleep(0.5);
            }
            System.out.println("  Ladders (lucky you!):");
            for (int[] ladder : ladderData) {
                System.out.println("    [+] Bottom: sq " + ladder[0] + "  -->  Top: sq " + ladder[1]);
                sleep(0.5);
            }
            sleep(1);
            System.out.println();

            // ── Mode select ──────────────────────────────────────────────────
            printSection("MODE SELECTION");
            sleep(1);
            System.out.println("  [Mode A] Normal Snakes and Ladders");
            sleep(0.5);
            System.out.println("  [Mode B] Override mode -- control the Robot's moves!");
            sleep(0.5);
            System.out.print("\n  Your choice: ");
            String modeselection = userimput.nextLine().trim().toLowerCase();
            sleep(1);

            // ════════════════════════════════════════════════════════════════
            //  MODE A
            // ════════════════════════════════════════════════════════════════
            if (modeselection.equals("mode a")) {

                printBanner("MODE A -- STANDARD GAME");
                sleep(1);
                System.out.println("  You (" + username + ") vs " + swiftbotname);
                sleep(0.5);
                System.out.println("  First to reach square 25 wins!\n");
                sleep(1);

                System.out.println("  THE BOARD:");
                sleep(0.5);
                for (String[] row : SnakesAndLaddersGameBoard) {
                    System.out.print("  ");
                    for (String sq : row) System.out.print("|" + sq + "|");
                    System.out.println();
                    sleep(0.3);
                }
                System.out.println();
                sleep(1);

                // ── Who goes first? ──────────────────────────────────────────
                printSection("ROLLING FOR FIRST TURN");
                sleep(1);
                int playerdie = (int) (Math.random() * 6) + 1;
                int swiftdie  = (int) (Math.random() * 6) + 1;
                System.out.println("  " + username     + " rolled: " + playerdie);
                sleep(0.5);
                System.out.println("  " + swiftbotname + " rolled: " + swiftdie);
                sleep(1);

                if (playerdie > swiftdie) {
                    playerTurn.set(true);
                    System.out.println("  --> " + username + " goes first!\n");
                    sleep(1);
                } else if (playerdie < swiftdie) {
                    playerTurn.set(false);
                    System.out.println("  --> " + swiftbotname + " goes first!\n");
                    sleep(1);
                } else {
                    System.out.println("  It's a tie! Rolling again...");
                    sleep(1);
                }

                int playerpostion = 1;
                int swiftposition = 1;
                int startpos      = 1;

                gameLoop:
                while (playerpostion < 25 && swiftposition < 25) {

                    printScore(username, playerpostion, swiftbotname, swiftposition);
                    sleep(1);

                    if (playerTurn.get()) {
                        // ── PLAYER'S TURN ────────────────────────────────
                        printSection(username.toUpperCase() + "'S TURN");
                        sleep(1);

                        api.disableAllButtons();
                        System.out.println("  Press [A] on the SwiftBot to roll the die...");
                        AtomicBoolean dieRolled = new AtomicBoolean(false);
                        api.enableButton(Button.A, () -> dieRolled.set(true));
                        while (!dieRolled.get()) { /* wait for button press */ }
                        api.disableAllButtons();

                        playerdie = (int) (Math.random() * 6) + 1;
                        sleep(0.5);
                        System.out.println("  Die rolled: " + playerdie);
                        sleep(1);

                        int oldPlayerPos = playerpostion;
                        playerpostion = applyMove(playerpostion, playerdie);
                        playerpostion = applySnakesAndLadders(playerpostion, username);
                        sleep(1);

                        System.out.println("  " + username + " moves: sq " + oldPlayerPos + " --> sq " + playerpostion);
                        sleep(1);

                        // ── Check square 5 BEFORE asking player to move piece ──
                        // This gives the player a chance to quit first
                        if (playerpostion == 5) {
                            writeLog("Checkpoint -- square 5 reached by " + username,
                                    username, playerpostion, swiftbotname, swiftposition);
                            sleep(0.5);
                            printSection("CHECKPOINT -- SQUARE 5");
                            sleep(1);
                            System.out.println("  " + username + " landed on square 5!");
                            sleep(0.5);
                            System.out.println("  Press [B] to quit the game.");
                            System.out.println("  Press [A] to keep playing...");
                            sleep(1);

                            AtomicBoolean keepPlaying = new AtomicBoolean(false);
                            AtomicBoolean quitting    = new AtomicBoolean(false);

                            api.disableAllButtons();
                            api.enableButton(Button.A, () -> keepPlaying.set(true));
                            api.enableButton(Button.B, () -> quitting.set(true));

                            // Wait here until player decides
                            while (!keepPlaying.get() && !quitting.get()) { /* wait */ }
                            api.disableAllButtons();

                            if (quitting.get()) {
                                printSection("GAME ENDED BY PLAYER");
                                System.out.println("  Thanks for playing, " + username + "!");
                                sleep(1);
                                System.exit(0);
                            }
                            System.out.println("  Continuing the game!\n");
                            sleep(1);
                        }

                        System.out.println("\n  Move your piece to square " + playerpostion + ".");
                        sleep(0.5);
                        System.out.println("  Lights are on -- press [A] again once you have moved.");
                        sleep(1);

                        int[] playerColour = {0, 0, 255};
                        api.fillUnderlights(playerColour);

                        AtomicBoolean turnDone = new AtomicBoolean(false);
                        api.enableButton(Button.A, () -> turnDone.set(true));
                        while (!turnDone.get()) { /* wait for player to move piece */ }

                        api.disableUnderlights();
                        api.disableAllButtons();
                        sleep(0.5);
                        System.out.println("  Move confirmed! Passing to " + swiftbotname + "...\n");
                        sleep(1);

                        playerTurn.set(false);

                    } else {
                        printSection(swiftbotname.toUpperCase() + "'S TURN");
                        sleep(1);

                        int swiftdiew = (int) (Math.random() * 6) + 1;
                        System.out.println("  " + swiftbotname + " rolls: " + swiftdiew);
                        sleep(1);

                        int oldSwiftPos = swiftposition;
                        swiftposition = applyMove(swiftposition, swiftdiew);
                        swiftposition = applySnakesAndLadders(swiftposition, swiftbotname);
                        sleep(1);

                        System.out.println("  " + swiftbotname + " moves: sq " + oldSwiftPos + " --> sq " + swiftposition);
                        sleep(1);

                        startpos = moveRobotToSquare(api, startpos, swiftposition);
                        sleep(2);

                        if (swiftposition == 5) {
                            writeLog("Checkpoint -- square 5 reached by " + swiftbotname,
                                    username, playerpostion, swiftbotname, swiftposition);
                            sleep(0.5);
                            printSection("CHECKPOINT -- SQUARE 5");
                            sleep(1);
                            System.out.println("  " + swiftbotname + " landed on square 5!");
                            sleep(0.5);
                            System.out.println("  Press [B] to quit the game.");
                            System.out.println("  Press [A] to keep playing...");
                            sleep(1);

                            AtomicBoolean keepPlaying = new AtomicBoolean(false);
                            AtomicBoolean quitting    = new AtomicBoolean(false);

                            api.disableAllButtons();
                            api.enableButton(Button.A, () -> keepPlaying.set(true));
                            api.enableButton(Button.B, () -> quitting.set(true));

                            while (!keepPlaying.get() && !quitting.get()) { /* wait */ }
                            api.disableAllButtons();

                            if (quitting.get()) {
                                printSection("GAME ENDED BY PLAYER");
                                System.out.println("  Thanks for playing, " + username + "!");
                                sleep(1);
                                System.exit(0);
                            }
                            System.out.println("  Continuing the game!\n");
                            sleep(1);
                        }

                        playerTurn.set(true);
                    }

                    if (playerpostion == 25) {
                        writeLog(username + " won the game!",
                                username, playerpostion, swiftbotname, swiftposition);
                        sleep(0.5);
                        printBanner("*** " + username.toUpperCase() + " WINS! ***");
                        sleep(1);
                        System.out.println("  Congratulations " + username + "! You reached square 25!");
                        sleep(2);
                        break gameLoop;
                    }
                    if (swiftposition == 25) {
                        writeLog(swiftbotname + " won the game!",
                                username, playerpostion, swiftbotname, swiftposition);
                        sleep(0.5);
                        printBanner("*** " + swiftbotname.toUpperCase() + " WINS! ***");
                        sleep(1);
                        System.out.println("  Better luck next time, " + username + "!");
                        sleep(2);
                        break gameLoop;
                    }
                }

                // ════════════════════════════════════════════════════════════════
                //  MODE B
                // ════════════════════════════════════════════════════════════════
            } else if (modeselection.equals("mode b")) {

                printBanner("MODE B -- OVERRIDE MODE");
                sleep(1);
                System.out.println("  You (" + username + ") vs " + swiftbotname);
                sleep(0.5);
                System.out.println("  When it is the Robot's turn, you may get a chance");
                sleep(0.5);
                System.out.println("  to override its dice roll -- up to 5 squares!");
                sleep(0.5);
                System.out.println("  First to reach square 25 wins!\n");
                sleep(1);

                System.out.println("  THE BOARD:");
                sleep(0.5);
                for (String[] row : SnakesAndLaddersGameBoard) {
                    System.out.print("  ");
                    for (String sq : row) System.out.print("|" + sq + "|");
                    System.out.println();
                    sleep(0.3);
                }
                System.out.println();
                sleep(1);

                printSection("ROLLING FOR FIRST TURN");
                sleep(1);
                int playerdie = (int) (Math.random() * 6) + 1;
                int swiftdie  = (int) (Math.random() * 6) + 1;
                System.out.println("  " + username     + " rolled: " + playerdie);
                sleep(0.5);
                System.out.println("  " + swiftbotname + " rolled: " + swiftdie);
                sleep(1);

                if (playerdie > swiftdie) {
                    playerTurn.set(true);
                    System.out.println("  --> " + username + " goes first!\n");
                    sleep(1);
                } else if (playerdie < swiftdie) {
                    playerTurn.set(false);
                    System.out.println("  --> " + swiftbotname + " goes first!\n");
                    sleep(1);
                } else {
                    System.out.println("  It's a tie! Rolling again...");
                    sleep(1);
                }

                int playerpostion = 1;
                int swiftposition = 1;
                int startpos      = 1;

                gameLoopB:
                while (playerpostion < 25 && swiftposition < 25) {

                    printScore(username, playerpostion, swiftbotname, swiftposition);
                    sleep(1);

                    if (playerTurn.get()) {
                        // ── PLAYER'S TURN ────────────────────────────────
                        printSection(username.toUpperCase() + "'S TURN");
                        sleep(1);

                        api.disableAllButtons();
                        System.out.println("  Press [A] on the SwiftBot to roll the die...");
                        AtomicBoolean dieRolled = new AtomicBoolean(false);
                        api.enableButton(Button.A, () -> dieRolled.set(true));
                        while (!dieRolled.get()) { /* wait */ }
                        api.disableAllButtons();

                        playerdie = (int) (Math.random() * 6) + 1;
                        sleep(0.5);
                        System.out.println("  Die rolled: " + playerdie);
                        sleep(1);

                        int oldPlayerPos = playerpostion;
                        playerpostion = applyMove(playerpostion, playerdie);
                        playerpostion = applySnakesAndLadders(playerpostion, username);
                        sleep(1);

                        System.out.println("  " + username + " moves: sq " + oldPlayerPos + " --> sq " + playerpostion);
                        sleep(1);

                        if (playerpostion == 5) {
                            writeLog("Checkpoint -- square 5 reached by " + username,
                                    username, playerpostion, swiftbotname, swiftposition);
                            sleep(0.5);
                            printSection("CHECKPOINT -- SQUARE 5");
                            sleep(1);
                            System.out.println("  " + username + " landed on square 5!");
                            sleep(0.5);
                            System.out.println("  Press [B] to quit the game.");
                            System.out.println("  Press [A] to keep playing...");
                            sleep(1);

                            AtomicBoolean keepPlaying = new AtomicBoolean(false);
                            AtomicBoolean quitting    = new AtomicBoolean(false);

                            api.disableAllButtons();
                            api.enableButton(Button.A, () -> keepPlaying.set(true));
                            api.enableButton(Button.B, () -> quitting.set(true));

                            while (!keepPlaying.get() && !quitting.get()) { /* wait */ }
                            api.disableAllButtons();

                            if (quitting.get()) {
                                printSection("GAME ENDED BY PLAYER");
                                System.out.println("  Thanks for playing, " + username + "!");
                                sleep(1);
                                System.exit(0);
                            }
                            System.out.println("  Continuing the game!\n");
                            sleep(1);
                        }

                        // ── Underlights on while player moves piece ───────
                        System.out.println("\n  Move your piece to square " + playerpostion + ".");
                        sleep(0.5);
                        System.out.println("  Lights are on -- press [A] again once you have moved.");
                        sleep(1);

                        int[] playerColour = {0, 0, 255};
                        api.fillUnderlights(playerColour);

                        AtomicBoolean turnDone = new AtomicBoolean(false);
                        api.enableButton(Button.A, () -> turnDone.set(true));
                        while (!turnDone.get()) { /* wait */ }

                        api.disableUnderlights();
                        api.disableAllButtons();
                        sleep(0.5);
                        System.out.println("  Move confirmed! Passing to " + swiftbotname + "...\n");
                        sleep(1);

                        playerTurn.set(false);

                    } else {
                        // ── SWIFTBOT'S TURN (with possible override) ──────
                        printSection(swiftbotname.toUpperCase() + "'S TURN");
                        sleep(1);

                        int swiftdiew = (int) (Math.random() * 6) + 1;
                        System.out.println("  " + swiftbotname + " rolls: " + swiftdiew);
                        sleep(1);

                        if (swiftposition != 1) {
                            int wheelnumber = (int) (Math.random() * 101);
                            System.out.println("  Wheel of Fortune spins... (" + wheelnumber + ")");
                            sleep(1);

                            if (wheelnumber % 2 == 0) {
                                printSection("OVERRIDE ACTIVATED");
                                sleep(1);
                                System.out.println("  You may move the Robot up to 5 squares!");
                                sleep(0.5);
                                Scanner wheelscanner = new Scanner(System.in);
                                System.out.print("  How many squares? (1-5): ");
                                int readingline2 = -1;
                                while (readingline2 < 1 || readingline2 > 5) {
                                    try {
                                        readingline2 = Integer.parseInt(wheelscanner.nextLine().trim());
                                        if (readingline2 < 1 || readingline2 > 5)
                                            System.out.print("  Invalid! Enter 1-5: ");
                                    } catch (NumberFormatException e) {
                                        System.out.print("  Not a number! Enter 1-5: ");
                                    }
                                }
                                System.out.print("  Forwards or Backwards? ");
                                String forOrBack = wheelscanner.nextLine().trim();
                                sleep(0.5);

                                int oldSwiftPos = swiftposition;
                                if (forOrBack.equals("Forwards")) {
                                    swiftposition = applyMove(swiftposition, readingline2);
                                } else if (forOrBack.equals("Backwards")) {
                                    swiftposition = Math.max(1, swiftposition - readingline2);
                                } else {
                                    System.out.println("  Invalid direction -- Robot rolls its own die.");
                                    swiftposition = applyMove(swiftposition, swiftdiew);
                                }
                                swiftposition = applySnakesAndLadders(swiftposition, swiftbotname);
                                sleep(1);
                                System.out.println("  " + swiftbotname + " moves: sq " + oldSwiftPos + " --> sq " + swiftposition);
                                sleep(1);

                            } else {
                                System.out.println("  " + wheeloffortune[1] + " -- Robot rolls normally.");
                                sleep(1);
                                int oldSwiftPos = swiftposition;
                                swiftposition = applyMove(swiftposition, swiftdiew);
                                swiftposition = applySnakesAndLadders(swiftposition, swiftbotname);
                                sleep(1);
                                System.out.println("  " + swiftbotname + " moves: sq " + oldSwiftPos + " --> sq " + swiftposition);
                                sleep(1);
                            }

                        } else {
                            int oldSwiftPos = swiftposition;
                            swiftposition = applyMove(swiftposition, swiftdiew);
                            swiftposition = applySnakesAndLadders(swiftposition, swiftbotname);
                            sleep(1);
                            System.out.println("  " + swiftbotname + " moves: sq " + oldSwiftPos + " --> sq " + swiftposition);
                            sleep(1);
                        }

                        startpos = moveRobotToSquare(api, startpos, swiftposition);
                        sleep(2);

                        // ── Check square 5 for SwiftBot ───────────────────
                        if (swiftposition == 5) {
                            writeLog("Checkpoint -- square 5 reached by " + swiftbotname,
                                    username, playerpostion, swiftbotname, swiftposition);
                            sleep(0.5);
                            printSection("CHECKPOINT -- SQUARE 5");
                            sleep(1);
                            System.out.println("  " + swiftbotname + " landed on square 5!");
                            sleep(0.5);
                            System.out.println("  Press [B] to quit the game.");
                            System.out.println("  Press [A] to keep playing...");
                            sleep(1);

                            AtomicBoolean keepPlaying = new AtomicBoolean(false);
                            AtomicBoolean quitting    = new AtomicBoolean(false);

                            api.disableAllButtons();
                            api.enableButton(Button.A, () -> keepPlaying.set(true));
                            api.enableButton(Button.B, () -> quitting.set(true));

                            while (!keepPlaying.get() && !quitting.get()) { /* wait */ }
                            api.disableAllButtons();

                            if (quitting.get()) {
                                printSection("GAME ENDED BY PLAYER");
                                System.out.println("  Thanks for playing, " + username + "!");
                                sleep(1);
                                System.exit(0);
                            }
                            System.out.println("  Continuing the game!\n");
                            sleep(1);
                        }

                        playerTurn.set(true);
                    }

                    // ── Win check with log ────────────────────────────────
                    if (playerpostion == 25) {
                        writeLog(username + " won the game!",
                                username, playerpostion, swiftbotname, swiftposition);
                        sleep(0.5);
                        printBanner("*** " + username.toUpperCase() + " WINS! ***");
                        sleep(1);
                        System.out.println("  Congratulations " + username + "! You reached square 25!");
                        sleep(2);
                        break gameLoopB;
                    }
                    if (swiftposition == 25) {
                        writeLog(swiftbotname + " won the game!",
                                username, playerpostion, swiftbotname, swiftposition);
                        sleep(0.5);
                        printBanner("*** " + swiftbotname.toUpperCase() + " WINS! ***");
                        sleep(1);
                        System.out.println("  Better luck next time, " + username + "!");
                        sleep(2);
                        break gameLoopB;
                    }
                }
            }
        }
    }


    static int applyMove(int currentPos, int roll) {
        int spacesLeft = 25 - currentPos;
        if (roll > spacesLeft) {
            System.out.println("  Cannot move -- would overshoot 25! Staying on square " + currentPos + ".");
            sleep(1);
            return currentPos;
        }
        return currentPos + roll;
    }


    static int applySnakesAndLadders(int position, String playerName) {
        for (int[] snake : snakeData) {
            if (snake[0] == position) {
                // Flash red for snake
                flashSnake();
                System.out.println("  >_< SNAKE! " + playerName + " lands on sq " + snake[0]
                        + " and slides down to sq " + snake[1] + "!");
                sleep(1);
                return snake[1];
            }
        }
        for (int[] ladder : ladderData) {
            if (ladder[0] == position) {
                // Flash yellow for ladder
                flashLadder();
                System.out.println("  /\\ LADDER! " + playerName + " lands on sq " + ladder[0]
                        + " and climbs up to sq " + ladder[1] + "!");
                sleep(1);
                return ladder[1];
            }
        }
        return position;
    }


    static int moveRobotToSquare(SwiftBotAPI api, int startPos, int targetSquare) {
        int currentPos = startPos;
        System.out.println("  [Robot] Navigating from sq " + currentPos + " to sq " + targetSquare);
        sleep(1);

        while (currentPos != targetSquare) {
            int nextPos = currentPos + 1;
            if (isRowEndSquare(currentPos)) {
                if (isEvenRowEnd(currentPos)) {
                    makingaleftcorner(currentPos);
                } else {
                    makingarightturn(currentPos);
                }
            } else {
                System.out.println("  [Robot] Moving straight -> sq " + nextPos);
                api.move(80, 100, 897);
                sleep(1);
            }
            currentPos = nextPos;
        }

        api.stopMove();
        sleep(0.5);
        System.out.println("  [Robot] Arrived at square " + targetSquare);
        sleep(1);
        return currentPos;
    }

    static boolean isRowEndSquare(int square) {
        return square == 5 || square == 10 || square == 15 || square == 20;
    }

    static boolean isEvenRowEnd(int square) {
        return square == 5 || square == 15;
    }

    static double sleep(double n) {
        try {
            n *= 1000;
            Thread.sleep((long) n);
            return n;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    static int makingaleftcorner(int pos) {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;
        System.out.println("  [Robot] Turning left (row transition)");
        sleep(0.5);
        api.move(0, 100, 725);   // turn
        sleep(1);
        api.move(80, 100, 465);  // forward one square width
        sleep(1);
        api.move(0, 100, 850);   // INCREASED from 655 to 900 for full 90 degree turn
        sleep(1);
        System.out.println("  [Robot] Left turn complete");
        sleep(0.5);
        return pos - 1;
    }

    static void makingarightturn(int pos) {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;
        System.out.println("  [Robot] Turning right (row transition)");
        sleep(0.5);
        api.move(90, 0, 725);    // turn
        sleep(1);
        api.move(80, 100, 495);  // forward one square width
        sleep(1);
        api.move(90, 0, 850);    // INCREASED from 725 to 900 for full 90 degree turn
        sleep(1);
        System.out.println("  [Robot] Right turn complete");
        sleep(0.5);
    }

    static void makingaleftcornerbackwards(int pos) {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;
        int i = -1;
        api.move(80, 0, 725);
        sleep(1);
        api.move(80 * i, 100 * i, 465);
        sleep(1);
        api.move(80, 0, 900);
        sleep(1);
    }

    static void makingarightturnbackwards(int pos) {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;
        int i = -1;
        api.move(0, 90, 725);
        sleep(1);
        api.move(80 * i, 100 * i, 495);
        sleep(1);
        api.move(0, 90, 900);
        sleep(1);
    }

    static void snakes(int startingpos) {
        int snake = 2;
        int[][] snakes = new int[snake][2];
        snakes[0][0] = 17; snakes[0][1] = 7;
        snakes[1][0] = 12; snakes[1][1] = 4;
    }

    static void ladder() {
        int ladder = 2;
        int[][] ladders = new int[ladder][2];
        ladders[0][0] = 3;  ladders[0][1] = 14;
        ladders[1][0] = 8;  ladders[1][1] = 19;
    }

    static void userfinish() {
        SwiftBotAPI api = SwiftBotAPI.INSTANCE;
        AtomicBoolean playerTurn    = new AtomicBoolean(true);
        AtomicBoolean playerrolldie = new AtomicBoolean(false);

        api.disableAllButtons();
        api.enableButton(Button.A, () -> {
            playerTurn.set(false);
            playerrolldie.set(true);
            System.out.println("  Player started turn!");
            int[] red = {255, 0, 0};
            api.fillUnderlights(red);
            api.disableAllButtons();
            api.enableButton(Button.A, () -> {
                playerrolldie.set(false);
                api.disableUnderlights();
                System.out.println("  Player turn over!");
                playerTurn.set(true);
            });
        });
    }
}

