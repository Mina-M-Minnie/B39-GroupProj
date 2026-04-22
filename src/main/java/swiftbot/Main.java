package swiftbot;

import java.io.PrintStream;
import java.util.Scanner;

import swiftbot.SpyBot.SpyBot;

public class Main {

	public static void main(String[] args) throws Exception {
		System.setOut(new PrintStream(System.out, true, "UTF-8"));
		Scanner scanner = new Scanner(System.in);

		boolean running = true;

		while (running) {

			//Print Menu
            System.out.println("\n==============================");
            System.out.println("        SwiftBot Menu         ");
            System.out.println("==============================");
			System.out.println("");
			System.out.println("Please select a program to run:");
            System.out.println("1. SpyBot");
			System.out.println("2. Traffic Light");
			System.out.println("3. Snakes and Ladders");
			System.out.println("4. Search for Light");
			System.out.println("5. Noughts and Crosses");
			System.out.println("6. Master Mind");
			System.out.println("7. Draw a Shape");
			System.out.println("8. ZigZag");
			System.out.println("9. ");
            System.out.println("10. Exit");
			System.out.println("");
            System.out.println("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
				// Nafiul
				// Runs the SpyBot program when option 1 is selected
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
                    System.out.println("\nStarting Traffic Light...\n");
                    try {
                        // Eman
						// Runs the Traffic Light program when option 2 is selected
                    } catch (Exception e) {
                        System.out.println("Error starting Traffic Light: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "3":
                    System.out.println("\nStarting Snakes and Ladders...\n");
                    try {
                        // Mina
						// Runs the Snakes and Ladders program when option 3 is selected
                    } catch (Exception e) {
                        System.out.println("Error starting Snakes and Ladders: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "4":
                    System.out.println("\nStarting Search for Light...\n");
                    try {
                        // Aaron
						// Runs the Search for Light program when option 4 is selected
                    } catch (Exception e) {
                        System.out.println("Error starting Search for Light: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "5":
                    System.out.println("\nStarting Noughts and Crosses...\n");
                    try {
                        // Noor
						// Runs the Noughts and Crosses program when option 5 is selected
                    } catch (Exception e) {
                        System.out.println("Error starting Noughts and Crosses: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "6":
                    System.out.println("\nStarting Master Mind...\n");
                    try {
                        // Saskia
						// Runs the Master Mind program when option 6 is selected
                    } catch (Exception e) {
                        System.out.println("Error starting Master Mind: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "7":
                    System.out.println("\nStarting Draw a Shape...\n");
                    try {
                        // Arkin
						// Runs the Draw a Shape program when option 7 is selected
                    } catch (Exception e) {
                        System.out.println("Error starting Draw a Shape: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "8":
                    System.out.println("\nStarting ZigZag...\n");
                    try {
                        // Naimul
						// Runs the ZigZag program when option 8 is selected
                    } catch (Exception e) {
                        System.out.println("Error starting ZigZag: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "9":
                    System.out.println("\nStarting ...\n");
                    try {
                        // Run the program for option 9 when implemented
                    } catch (Exception e) {
                        System.out.println("Error starting : " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case "10":
                    System.out.println("Exiting program...");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1 or 2.");
            }
        }

        scanner.close();
	}

}