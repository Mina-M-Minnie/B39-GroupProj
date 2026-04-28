package swiftbot.NoughtsAndCrosses;

import swiftbot.SwiftBotAPI;
import swiftbot.Button;
import swiftbot.ButtonFunction;
import swiftbot.Underlight;

import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Noughts and Crosses (Tic-Tac-Toe) for SwiftBot API 6.0.0
 *
 * Board layout (each cell = 25 cm):
 *        Col1  Col2  Col3
 *  Row1 [ 1,1][1,2][1,3]
 *  Row2 [ 2,1][2,2][2,3]
 *  Row3 [ 3,1][3,2][3,3]
 *
 * HOME: bot starts below [3,2], facing NORTH
 */
public class NoughtsAndCrosses {

    // SwiftBot + input
    static final SwiftBotAPI bot = SwiftBotAPI.INSTANCE;
    static final Scanner scanner = new Scanner(System.in);

    // Calibration settings
    static int DRIVE_LEFT_SPEED = 40;
    static int DRIVE_RIGHT_SPEED = 40;

    static int TURN_RIGHT_LEFT_WHEEL = 35;
    static int TURN_RIGHT_RIGHT_WHEEL = -35;

    static int TURN_LEFT_LEFT_WHEEL = -35;
    static int TURN_LEFT_RIGHT_WHEEL = 35;

    static int TURN_90_MS = 800;
    static int CELL_MS = 1100;
    static int PAUSE_MS = 200;

    // Bot tracking
    static final int HOME_ROW = 0;
    static final int HOME_COL = 2;
    static int botRow = HOME_ROW;
    static int botCol = HOME_COL;
    static int facing = 0; // 0=N,1=E,2=S,3=W

    // Game state
    static char[][] board = new char[4][4];
    static String playerName;
    static final String BOT_NAME = "SwiftBot";
    static char playerPiece, botPiece;
    static int playerScore = 0, botScore = 0;
    static int roundNumber = 0;
    static final List<String> moveLog = new ArrayList<>();

    // Button flags
    static volatile boolean buttonAPressed = false;
    static volatile boolean buttonXPressed = false;
    static volatile boolean buttonYPressed = false;

    // ENTRY POINT
    public static void main(String[] args) {
        bot.enableButton(Button.A, () -> buttonAPressed = true);
        bot.enableButton(Button.X, () -> buttonXPressed = true);
        bot.enableButton(Button.Y, () -> buttonYPressed = true);

        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║   NOUGHTS AND CROSSES – SwiftBot ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("Press button A on the SwiftBot to start...");
        waitForButtonA();

        runCalibrationMode();

        playerName = promptName();
        System.out.println("\nWelcome, " + playerName + "! You are playing against " + BOT_NAME + ".");

        boolean playing = true;
        while (playing) {
            roundNumber++;
            playRound();

            System.out.println("\nPress Y on the SwiftBot to play again, or X to quit.");
            buttonXPressed = false;
            buttonYPressed = false;
            char choice = waitForXorY();
            if (choice == 'X') {
                playing = false;
            }
        }

        printScoreboard();
        System.out.println("Thank you for playing! Goodbye.");
        bot.disableAllButtons();
    }

    static void playRound() {
        System.out.println("\n══════════  ROUND " + roundNumber + "  ══════════");
        initBoard();
        moveLog.clear();
        resetBotTrackingToHome();

        boolean playerFirst = rollForFirst();
        if (playerFirst) {
            playerPiece = 'O';
            botPiece = 'X';
            System.out.println(playerName + " goes first → O     " + BOT_NAME + " → X");
        } else {
            playerPiece = 'X';
            botPiece = 'O';
            System.out.println(BOT_NAME + " goes first → O     " + playerName + " → X");
        }

        boolean playerTurn = playerFirst;
        String winner = null;
        boolean draw = false;

        while (true) {
            printBoard();

            if (playerTurn) {
                int[] move = getPlayerMove();
                board[move[0]][move[1]] = playerPiece;
                moveLog.add(playerName + "," + playerPiece + "," + move[0] + "," + move[1]);
                System.out.printf("[%s – %c] moved to square [%d,%d]%n",
                        playerName, playerPiece, move[0], move[1]);
            } else {
                int[] move = getBotMove();
                board[move[0]][move[1]] = botPiece;
                moveLog.add(BOT_NAME + "," + botPiece + "," + move[0] + "," + move[1]);

                System.out.printf("[%s – %c] is moving to square [%d,%d]...%n",
                        BOT_NAME, botPiece, move[0], move[1]);

                botDriveToSquare(move[0], move[1]);
                blinkThreeTimes(0, 200, 0);
                botReturnHome();

                System.out.printf("[%s – %c] moved to square [%d,%d]%n",
                        BOT_NAME, botPiece, move[0], move[1]);
            }

            char w = checkWinner();
            if (w == playerPiece) {
                winner = playerName;
                playerScore++;
                break;
            }
            if (w == botPiece) {
                winner = BOT_NAME;
                botScore++;
                break;
            }
            if (isBoardFull()) {
                draw = true;
                break;
            }

            playerTurn = !playerTurn;
        }

        printBoard();

        if (draw) {
            System.out.println("It's a DRAW!");
            blinkThreeTimes(0, 0, 200);
            spinOnce();
            blinkThreeTimes(0, 0, 200);
        } else {
            System.out.println("🎉 " + winner + " WINS this round!");

            boolean oWins = (winner.equals(playerName) && playerPiece == 'O')
                    || (winner.equals(BOT_NAME) && botPiece == 'O');

            int[] rgb = oWins ? new int[]{0, 200, 0} : new int[]{200, 0, 0};

            blinkThreeTimes(rgb[0], rgb[1], rgb[2]);
            traceWinningLine();
            blinkThreeTimes(rgb[0], rgb[1], rgb[2]);
            botReturnHome();
        }

        writeRoundToFile(winner, draw);
        printScoreboard();
    }

    static void initBoard() {
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                board[r][c] = '.';
            }
        }
    }

    static void printBoard() {
        System.out.println("\n    1   2   3");
        for (int r = 1; r <= 3; r++) {
            System.out.print(r + "  ");
            for (int c = 1; c <= 3; c++) {
                System.out.print(" " + board[r][c] + " ");
                if (c < 3) System.out.print("|");
            }
            System.out.println();
            if (r < 3) System.out.println("   ---+---+---");
        }
        System.out.println();
    }

    static boolean isBoardFull() {
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                if (board[r][c] == '.') return false;
            }
        }
        return true;
    }

    static char checkWinner() {
        for (int r = 1; r <= 3; r++) {
            if (board[r][1] != '.' && board[r][1] == board[r][2] && board[r][2] == board[r][3]) {
                return board[r][1];
            }
        }
        for (int c = 1; c <= 3; c++) {
            if (board[1][c] != '.' && board[1][c] == board[2][c] && board[2][c] == board[3][c]) {
                return board[1][c];
            }
        }
        if (board[1][1] != '.' && board[1][1] == board[2][2] && board[2][2] == board[3][3]) {
            return board[1][1];
        }
        if (board[1][3] != '.' && board[1][3] == board[2][2] && board[2][2] == board[3][1]) {
            return board[1][3];
        }
        return '.';
    }

    static String promptName() {
        String name = "";
        while (name.trim().isEmpty()) {
            System.out.print("Enter your name: ");
            try {
                name = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                name = "Player";
            }
            if (name.isEmpty()) {
                System.out.println("⚠ Name cannot be empty. Please try again.");
            }
        }
        return name;
    }

    static int[] getPlayerMove() {
        while (true) {
            System.out.print("Your move [row,col] e.g. 2,3 : ");
            String input;
            try {
                input = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                System.out.println("⚠ Input error, try again.");
                continue;
            }

            try {
                String[] parts = input.split(",");
                if (parts.length != 2) throw new IllegalArgumentException();

                int row = Integer.parseInt(parts[0].trim());
                int col = Integer.parseInt(parts[1].trim());

                if (row < 1 || row > 3 || col < 1 || col > 3) {
                    System.out.println("⚠ Row and column must each be 1, 2 or 3.");
                    continue;
                }
                if (board[row][col] != '.') {
                    System.out.println("⚠ [" + row + "," + col + "] is already occupied.");
                    continue;
                }

                return new int[]{row, col};
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ Invalid format. Use row,col e.g. 2,3");
            }
        }
    }

    static int[] getBotMove() {
        int[] move;

        move = findStrategicMove(botPiece);
        if (move != null) return move;

        move = findStrategicMove(playerPiece);
        if (move != null) return move;

        if (board[2][2] == '.') return new int[]{2, 2};

        for (int[] c : new int[][]{{1, 1}, {1, 3}, {3, 1}, {3, 3}}) {
            if (board[c[0]][c[1]] == '.') return c;
        }

        List<int[]> empty = new ArrayList<>();
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                if (board[r][c] == '.') empty.add(new int[]{r, c});
            }
        }
        return empty.get(new Random().nextInt(empty.size()));
    }

    static int[] findStrategicMove(char piece) {
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                if (board[r][c] == '.') {
                    board[r][c] = piece;
                    boolean wins = checkWinner() == piece;
                    board[r][c] = '.';
                    if (wins) return new int[]{r, c};
                }
            }
        }
        return null;
    }

    static boolean rollForFirst() {
        Random rand = new Random();
        int pRoll, bRoll;

        System.out.println("\n--- Rolling to see who goes first ---");
        do {
            pRoll = rand.nextInt(6) + 1;
            bRoll = rand.nextInt(6) + 1;

            System.out.println(playerName + " rolled: " + pRoll);
            System.out.println(BOT_NAME + " rolled: " + bRoll);

            if (pRoll == bRoll) {
                System.out.println("It's a Tie! Rolling again...");
            }
        } while (pRoll == bRoll);

        return pRoll > bRoll;
    }

    static void botDriveToSquare(int targetRow, int targetCol) {
        navigateTo(targetRow, targetCol);
        botRow = targetRow;
        botCol = targetCol;
    }

    static void botReturnHome() {
        System.out.println(BOT_NAME + " returning to home...");
        navigateTo(HOME_ROW, HOME_COL);
        botRow = HOME_ROW;
        botCol = HOME_COL;
        turnToFacing(0);
    }

    static void navigateTo(int targetRow, int targetCol) {
        int dr = targetRow - botRow;
        int dc = targetCol - botCol;

        if (dr != 0) {
            turnToFacing(dr > 0 ? 2 : 0);
            driveForward(Math.abs(dr));
        }
        if (dc != 0) {
            turnToFacing(dc > 0 ? 1 : 3);
            driveForward(Math.abs(dc));
        }
    }

    static void turnToFacing(int target) {
        int diff = (target - facing + 4) % 4;

        if (diff == 0) return;
        if (diff == 1) {
            turnRight90();
        } else if (diff == 2) {
            turnRight90();
            turnRight90();
        } else {
            turnLeft90();
        }

        facing = target;
    }

    static void driveForward(int cells) {
        bot.move(DRIVE_LEFT_SPEED, DRIVE_RIGHT_SPEED, cells * CELL_MS);
        pause(PAUSE_MS);
    }

    static void turnRight90() {
        bot.move(TURN_RIGHT_LEFT_WHEEL, TURN_RIGHT_RIGHT_WHEEL, TURN_90_MS);
        pause(PAUSE_MS);
    }

    static void turnLeft90() {
        bot.move(TURN_LEFT_LEFT_WHEEL, TURN_LEFT_RIGHT_WHEEL, TURN_90_MS);
        pause(PAUSE_MS);
    }

    static void spinOnce() {
        bot.move(TURN_RIGHT_LEFT_WHEEL, TURN_RIGHT_RIGHT_WHEEL, TURN_90_MS * 4);
        pause(300);
    }

    static void resetBotTrackingToHome() {
        botRow = HOME_ROW;
        botCol = HOME_COL;
        facing = 0;
    }

    static void traceWinningLine() {
        int[][] line = getWinningLine();
        if (line == null) return;

        botDriveToSquare(line[0][0], line[0][1]);
        pause(400);

        navigateTo(line[1][0], line[1][1]);
        botRow = line[1][0];
        botCol = line[1][1];
        pause(400);

        navigateTo(line[2][0], line[2][1]);
        botRow = line[2][0];
        botCol = line[2][1];
        pause(400);
    }

    static int[][] getWinningLine() {
        char w = checkWinner();
        if (w == '.') return null;

        for (int r = 1; r <= 3; r++) {
            if (board[r][1] == w && board[r][2] == w && board[r][3] == w) {
                return new int[][]{{r, 1}, {r, 2}, {r, 3}};
            }
        }
        for (int c = 1; c <= 3; c++) {
            if (board[1][c] == w && board[2][c] == w && board[3][c] == w) {
                return new int[][]{{1, c}, {2, c}, {3, c}};
            }
        }
        if (board[1][1] == w && board[2][2] == w && board[3][3] == w) {
            return new int[][]{{1, 1}, {2, 2}, {3, 3}};
        }
        if (board[1][3] == w && board[2][2] == w && board[3][1] == w) {
            return new int[][]{{1, 3}, {2, 2}, {3, 1}};
        }
        return null;
    }

    static void blinkThreeTimes(int r, int g, int b) {
        int[] on = {r, g, b};
        int[] off = {0, 0, 0};

        for (int i = 0; i < 3; i++) {
            bot.fillUnderlights(on);
            pause(400);
            bot.fillUnderlights(off);
            pause(400);
        }
    }

    static void waitForButtonA() {
        buttonAPressed = false;
        while (!buttonAPressed) {
            pause(50);
        }
        buttonAPressed = false;
    }

    static char waitForXorY() {
        while (true) {
            if (buttonXPressed) {
                buttonXPressed = false;
                return 'X';
            }
            if (buttonYPressed) {
                buttonYPressed = false;
                return 'Y';
            }
            pause(50);
        }
    }

    static void writeRoundToFile(String winner, boolean draw) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("game_log.txt", true))) {
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            pw.println("=== ROUND " + roundNumber + " ===");
            pw.println("Date/Time : " + ts);
            pw.println("Player Name: " + playerName);
            pw.println("Bot Name   : " + BOT_NAME);
            pw.println("Player Piece: " + playerPiece);
            pw.println("Bot Piece   : " + botPiece);
            pw.println("Moves (player,piece,row,col):");

            for (String move : moveLog) {
                pw.println("  " + move);
            }

            pw.println("Result     : " + (draw ? "DRAW" : "WIN"));
            pw.println("Winner     : " + (draw ? "None" : winner));
            pw.println("Scores     : " + playerName + "=" + playerScore + " | " + BOT_NAME + "=" + botScore);
            pw.println();
        } catch (IOException e) {
            System.out.println("⚠ Could not write log: " + e.getMessage());
        }
    }

    static void printScoreboard() {
        System.out.println("\n──── SCOREBOARD ────");
        System.out.printf("  %-15s %d%n", playerName, playerScore);
        System.out.printf("  %-15s %d%n", BOT_NAME, botScore);
        System.out.println("────────────────────");
    }

    static void runCalibrationMode() {
        System.out.print("Enter calibration mode first? (y/n): ");
        String choice;

        try {
            choice = scanner.nextLine().trim().toLowerCase();
        } catch (Exception e) {
            return;
        }

        if (!choice.equals("y")) return;

        System.out.println("\n=== CALIBRATION MODE ===");
        System.out.println("Place the robot at HOME: below [3,2], facing NORTH.");

        while (true) {
            printCalibrationSettings();

            System.out.println("\nChoose an option:");
            System.out.println("1  - Drive forward 1 cell");
            System.out.println("2  - Turn right 90");
            System.out.println("3  - Turn left 90");
            System.out.println("4  - Spin once");
            System.out.println("5  - Drive to square [row,col]");
            System.out.println("6  - Return home");
            System.out.println("7  - Reset tracked position to HOME facing NORTH");
            System.out.println("8  - Change CELL_MS");
            System.out.println("9  - Change TURN_90_MS");
            System.out.println("10 - Change DRIVE_LEFT_SPEED");
            System.out.println("11 - Change DRIVE_RIGHT_SPEED");
            System.out.println("12 - Change right turn wheel speeds");
            System.out.println("13 - Change left turn wheel speeds");
            System.out.println("0  - Exit calibration mode");
            System.out.print("Option: ");

            String input;
            try {
                input = scanner.nextLine().trim();
            } catch (Exception e) {
                System.out.println("Input error.");
                continue;
            }

            switch (input) {
                case "1":
                    System.out.println("Driving forward 1 cell...");
                    driveForward(1);
                    break;
                case "2":
                    System.out.println("Turning right 90...");
                    turnRight90();
                    break;
                case "3":
                    System.out.println("Turning left 90...");
                    turnLeft90();
                    break;
                case "4":
                    System.out.println("Spinning once...");
                    spinOnce();
                    break;
                case "5":
                    driveToSquareFromCalibration();
                    break;
                case "6":
                    System.out.println("Returning home...");
                    botReturnHome();
                    break;
                case "7":
                    resetBotTrackingToHome();
                    System.out.println("Tracked position reset to HOME facing NORTH.");
                    break;
                case "8":
                    CELL_MS = promptForInt("Enter new CELL_MS: ", 100, 10000, CELL_MS);
                    break;
                case "9":
                    TURN_90_MS = promptForInt("Enter new TURN_90_MS: ", 100, 5000, TURN_90_MS);
                    break;
                case "10":
                    DRIVE_LEFT_SPEED = promptForInt("Enter new DRIVE_LEFT_SPEED: ", -100, 100, DRIVE_LEFT_SPEED);
                    break;
                case "11":
                    DRIVE_RIGHT_SPEED = promptForInt("Enter new DRIVE_RIGHT_SPEED: ", -100, 100, DRIVE_RIGHT_SPEED);
                    break;
                case "12":
                    TURN_RIGHT_LEFT_WHEEL = promptForInt("Enter new TURN_RIGHT_LEFT_WHEEL: ", -100, 100, TURN_RIGHT_LEFT_WHEEL);
                    TURN_RIGHT_RIGHT_WHEEL = promptForInt("Enter new TURN_RIGHT_RIGHT_WHEEL: ", -100, 100, TURN_RIGHT_RIGHT_WHEEL);
                    break;
                case "13":
                    TURN_LEFT_LEFT_WHEEL = promptForInt("Enter new TURN_LEFT_LEFT_WHEEL: ", -100, 100, TURN_LEFT_LEFT_WHEEL);
                    TURN_LEFT_RIGHT_WHEEL = promptForInt("Enter new TURN_LEFT_RIGHT_WHEEL: ", -100, 100, TURN_LEFT_RIGHT_WHEEL);
                    break;
                case "0":
                    System.out.println("Exiting calibration mode...");
                    return;
                default:
                    System.out.println("Option is Invalid.");
            }

            System.out.printf("Tracked position: row=%d, col=%d, facing=%s%n",
                    botRow, botCol, facingToString());
        }
    }

    static void printCalibrationSettings() {
        System.out.println("\n=== CURRENT CALIBRATION SETTINGS ===");
        System.out.println("DRIVE_LEFT_SPEED         = " + DRIVE_LEFT_SPEED);
        System.out.println("DRIVE_RIGHT_SPEED        = " + DRIVE_RIGHT_SPEED);
        System.out.println("TURN_RIGHT_LEFT_WHEEL    = " + TURN_RIGHT_LEFT_WHEEL);
        System.out.println("TURN_RIGHT_RIGHT_WHEEL   = " + TURN_RIGHT_RIGHT_WHEEL);
        System.out.println("TURN_LEFT_LEFT_WHEEL     = " + TURN_LEFT_LEFT_WHEEL);
        System.out.println("TURN_LEFT_RIGHT_WHEEL    = " + TURN_LEFT_RIGHT_WHEEL);
        System.out.println("TURN_90_MS               = " + TURN_90_MS);
        System.out.println("CELL_MS                  = " + CELL_MS);
        System.out.println("PAUSE_MS                 = " + PAUSE_MS);
        System.out.println("====================================");
    }

    static int promptForInt(String prompt, int min, int max, int currentValue) {
        while (true) {
            System.out.print(prompt + " [current=" + currentValue + "]: ");
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);

                if (value < min || value > max) {
                    System.out.println("Value must be between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (Exception e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    static void driveToSquareFromCalibration() {
        try {
            System.out.print("Enter target square as row,col : ");
            String rc = scanner.nextLine().trim();
            String[] parts = rc.split(",");

            if (parts.length != 2) {
                System.out.println("Invalid format.");
                return;
            }

            int row = Integer.parseInt(parts[0].trim());
            int col = Integer.parseInt(parts[1].trim());

            if (row < 1 || row > 3 || col < 1 || col > 3) {
                System.out.println("Row and column must be between 1 and 3.");
                return;
            }

            System.out.printf("Driving to square [%d,%d]...%n", row, col);
            botDriveToSquare(row, col);
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    static String facingToString() {
        switch (facing) {
            case 0: return "NORTH";
            case 1: return "EAST";
            case 2: return "SOUTH";
            case 3: return "WEST";
            default: return "UNKNOWN";
        }
    }

    static void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}
    

