package swiftbot.NoughtsAndCrosses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import swiftbot.Button;
import swiftbot.SwiftBotAPI;

public class NoughtsAndCrosses {

    static final SwiftBotAPI bot = SwiftBotAPI.INSTANCE;
    static final Scanner scanner = new Scanner(System.in);

    static int DRIVE_LEFT_SPEED = 40;
    static int DRIVE_RIGHT_SPEED = 40;
    static int TURN_RIGHT_LEFT_WHEEL = 35;
    static int TURN_RIGHT_RIGHT_WHEEL = -35;
    static int TURN_LEFT_LEFT_WHEEL = -35;
    static int TURN_LEFT_RIGHT_WHEEL = 35;
    static int TURN_90_MS = 800;
    static int CELL_MS = 1100;
    static int PAUSE_MS = 200;

    static final int HOME_ROW = 0;
    static final int HOME_COL = 2;
    static int botRow = HOME_ROW;
    static int botCol = HOME_COL;
    static int facing = 0;

    static char[][] board = new char[4][4];
    static String playerName;
    static final String BOT_NAME = "SwiftBot";
    static char playerPiece, botPiece;
    static int playerScore = 0, botScore = 0;
    static int roundNumber = 0;
    static final List<String> moveLog = new ArrayList<>();

    static volatile boolean buttonAPressed = false;
    static volatile boolean buttonXPressed = false;
    static volatile boolean buttonYPressed = false;

    // ✅ FIXED ENTRY METHOD
    public void start() {
        bot.enableButton(Button.A, () -> buttonAPressed = true);
        bot.enableButton(Button.X, () -> buttonXPressed = true);
        bot.enableButton(Button.Y, () -> buttonYPressed = true);

        System.out.println("NOUGHTS AND CROSSES - SwiftBot");
        System.out.println("Press button A to start...");
        waitForButtonA();

        playerName = promptName();
        System.out.println("Welcome, " + playerName);

        boolean playing = true;

        while (playing) {
            roundNumber++;
            playRound();

            System.out.println("\nPress Y to play again or X to quit");

            buttonXPressed = false;
            buttonYPressed = false;

            char choice = waitForXorY();
            if (choice == 'X') {
                playing = false;
            }
        }

        printScoreboard();
        System.out.println("Goodbye!");
        bot.disableAllButtons();
    }

    static void playRound() {
        initBoard();
        moveLog.clear();
        resetBotTrackingToHome();

        boolean playerFirst = rollForFirst();

        playerPiece = playerFirst ? 'O' : 'X';
        botPiece = playerFirst ? 'X' : 'O';

        boolean playerTurn = playerFirst;

        while (true) {
            printBoard();

            if (playerTurn) {
                int[] move = getPlayerMove();
                board[move[0]][move[1]] = playerPiece;
            } else {
                int[] move = getBotMove();
                board[move[0]][move[1]] = botPiece;

                botDriveToSquare(move[0], move[1]);
                blinkThreeTimes(0, 200, 0);
                botReturnHome();
            }

            char winner = checkWinner();

            if (winner == playerPiece) {
                System.out.println(playerName + " wins!");
                playerScore++;
                break;
            }

            if (winner == botPiece) {
                System.out.println("SwiftBot wins!");
                botScore++;
                break;
            }

            if (isBoardFull()) {
                System.out.println("Draw!");
                break;
            }

            playerTurn = !playerTurn;
        }
    }

    static void initBoard() {
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                board[r][c] = '.';
            }
        }
    }

    static void printBoard() {
        System.out.println("\nBoard:");
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                System.out.print(board[r][c] + " ");
            }
            System.out.println();
        }
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
        for (int i = 1; i <= 3; i++) {
            if (board[i][1] != '.' && board[i][1] == board[i][2] && board[i][2] == board[i][3]) return board[i][1];
            if (board[1][i] != '.' && board[1][i] == board[2][i] && board[2][i] == board[3][i]) return board[1][i];
        }

        if (board[1][1] != '.' && board[1][1] == board[2][2] && board[2][2] == board[3][3]) return board[1][1];
        if (board[1][3] != '.' && board[1][3] == board[2][2] && board[2][2] == board[3][1]) return board[1][3];

        return '.';
    }

    static String promptName() {
        System.out.print("Enter your name: ");
        return scanner.nextLine().trim();
    }

    static int[] getPlayerMove() {
        while (true) {
            System.out.print("Enter move (row,col): ");
            try {
                String[] parts = scanner.nextLine().split(",");
                int r = Integer.parseInt(parts[0].trim());
                int c = Integer.parseInt(parts[1].trim());

                if (board[r][c] == '.') return new int[]{r, c};

            } catch (Exception ignored) {}
            System.out.println("Invalid move.");
        }
    }

    static int[] getBotMove() {
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                if (board[r][c] == '.') return new int[]{r, c};
            }
        }
        return new int[]{1,1};
    }

    static boolean rollForFirst() {
        return new Random().nextBoolean();
    }

    static void botDriveToSquare(int r, int c) {
        bot.move(40, 40, CELL_MS);
    }

    static void botReturnHome() {
        bot.move(-40, -40, CELL_MS);
    }

    static void resetBotTrackingToHome() {
        botRow = HOME_ROW;
        botCol = HOME_COL;
        facing = 0;
    }

    static void blinkThreeTimes(int r, int g, int b) {
        for (int i = 0; i < 3; i++) {
            bot.fillUnderlights(new int[]{r,g,b});
            pause(300);
            bot.disableUnderlights();
            pause(300);
        }
    }

    static void waitForButtonA() {
        while (!buttonAPressed) pause(50);
        buttonAPressed = false;
    }

    static char waitForXorY() {
        while (true) {
            if (buttonXPressed) return 'X';
            if (buttonYPressed) return 'Y';
            pause(50);
        }
    }

    static void printScoreboard() {
        System.out.println("\nScore:");
        System.out.println(playerName + ": " + playerScore);
        System.out.println("SwiftBot: " + botScore);
    }

    static void pause(int ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }
}