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
            System.out.println("1. Start SpyBot");
			System.out.println("2. ");
			System.out.println("3. ");
			System.out.println("4. ");
			System.out.println("5. ");
			System.out.println("6. ");
			System.out.println("7. ");
			System.out.println("8. ");
			System.out.println("9. ");
            System.out.println("10. Exit");
            System.out.println("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
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
                    System.out.println("\nStarting ...\n");
                    try {
                        // Call the start method of the corresponding program
                    } catch (Exception e) {
                        System.out.println("Error starting : " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "3":
                    System.out.println("\nStarting ...\n");
                    try {
                        // Call the start method of the corresponding program
                    } catch (Exception e) {
                        System.out.println("Error starting : " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "4":
                    System.out.println("\nStarting ...\n");
                    try {
                        // Call the start method of the corresponding program
                    } catch (Exception e) {
                        System.out.println("Error starting : " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "5":
                    System.out.println("\nStarting ...\n");
                    try {
                        // Call the start method of the corresponding program
                    } catch (Exception e) {
                        System.out.println("Error starting : " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "6":
                    System.out.println("\nStarting ...\n");
                    try {
                        // Call the start method of the corresponding program
                    } catch (Exception e) {
                        System.out.println("Error starting : " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "7":
                    System.out.println("\nStarting ...\n");
                    try {
                        // Call the start method of the corresponding program
                    } catch (Exception e) {
                        System.out.println("Error starting : " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "8":
                    System.out.println("\nStarting ...\n");
                    try {
                        // Call the start method of the corresponding program
                    } catch (Exception e) {
                        System.out.println("Error starting : " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

				case "9":
                    System.out.println("\nStarting ...\n");
                    try {
                        // Call the start method of the corresponding program
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