package swiftbot;

import java.io.PrintStream;
import java.util.Scanner;

import swiftbot.SearchForLight.SearchForLight;
import swiftbot.SnakesAndLadders.SnakesAndLadders;
import swiftbot.SpyBot.SpyBot;
import swiftbot.NoughtsAndCrosses.NoughtsAndCrosses;

public class Main {

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n==============================");
            System.out.println("        SwiftBot Menu         ");
            System.out.println("==============================");
            System.out.println();
            System.out.println("Please select a program to run:");
            System.out.println("1. SpyBot");
            System.out.println("2. Traffic Light");
            System.out.println("3. Snakes and Ladders");
            System.out.println("4. Search for Light");
            System.out.println("5. Noughts and Crosses");
            System.out.println("6. Master Mind");
            System.out.println("7. Draw a Shape");
            System.out.println("8. ZigZag");
            System.out.println("9. Unassigned");
            System.out.println("10. Exit");
            System.out.println();
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\nStarting SpyBot...\n");
                    try {
                        SpyBot program = new SpyBot();
                        program.start();
                    } catch (Exception e) {
                        System.out.println("Error starting SpyBot: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case "2":
                    System.out.println("\nTraffic Light is not linked yet.\n");
                    break;

                case "3":
                    System.out.println("\nStarting Snakes and Ladders...\n");
                    try {
                        SnakesAndLadders game = new SnakesAndLadders();
                        game.start();
                    } catch (Exception e) {
                        System.out.println("Error starting Snakes and Ladders: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case "4":
                    System.out.println("\nStarting Search for Light...\n");
                    try {
                        SearchForLight lightProgram = new SearchForLight();
                        lightProgram.init();
                    } catch (Exception e) {
                        System.out.println("Error starting Search for Light: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case "5":
                    System.out.println("\nStarting Noughts and Crosses...\n");
                    try {
                        NoughtsAndCrosses game = new NoughtsAndCrosses();
                        game.start();
                    } catch (Exception e) {
                        System.out.println("Error starting Noughts and Crosses: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case "6":
                    System.out.println("\nMaster Mind is not linked yet.\n");
                    break;

                case "7":
                    System.out.println("\nDraw a Shape is not linked yet.\n");
                    break;

                case "8":
                    System.out.println("\nZigZag is not linked yet.\n");
                    break;

                case "9":
                    System.out.println("\nOption 9 is not assigned yet.\n");
                    break;

                case "10":
                    System.out.println("Exiting program...");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please choose a number from 1 to 10.");
            }
        }

        scanner.close();
    }
}