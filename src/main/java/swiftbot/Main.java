

import java.io.PrintStream;
import java.util.Scanner;

import NoughtsAndCrosses.NoughtsAndCrosses;
import SearchForLight.SearchForLight;
import SnakesAndLadders.SnakesAndLadders;
import SpyBot.SpyBot;

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
                        //uses errors that throw "1" as a replacement for System.exit so it returns to the main menu
                        if (!e.getMessage().equals("1")) {
                            System.out.println("Error with SpyBot program: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;

                // Eman
				// Runs the Traffic Light program when option 2 is selected
				case "2":
                    System.out.println("\nStarting Traffic Light...\n");
                    try {
                        // Call the Start method for the Traffic Light program when implemented
                    } catch (Exception e) {
                        //uses errors that throw "1" as a replacement for System.exit so it returns to the main menu
                        if (!e.getMessage().equals("1")) {
                            System.out.println("Error with Traffic Light program: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;

                // Mina
				// Runs the Snakes and Ladders program when option 3 is selected
				case "3":
                    System.out.println("\nStarting Snakes and Ladders...\n");
                    try {
                        SnakesAndLadders.main(null);
                    } catch (Exception e) {
                        //uses errors that throw "1" as a replacement for System.exit so it returns to the main menu
                        if (!e.getMessage().equals("1")) {
                            System.out.println("Error with Snakes and Ladders program: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;

                // Aaron
				// Runs the Search for Light program when option 4 is selected
				case "4":
                    System.out.println("\nStarting Search for Light...\n");
                    try {
                        SearchForLight lightProgram = new SearchForLight();
                        lightProgram.init();
                    } catch (Exception e) {
                        //uses errors that throw "1" as a replacement for System.exit so it returns to the main menu
                        if (!e.getMessage().equals("1")) {
                            System.out.println("Error with Search for Light program: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;

                // Noor
				// Runs the Noughts and Crosses program when option 5 is selected
				case "5":
                    System.out.println("\nStarting Noughts and Crosses...\n");
                    try {
                        NoughtsAndCrosses.main(null);
                    } catch (Exception e) {
                        //uses errors that throw "1" as a replacement for System.exit so it returns to the main menu
                        if (!e.getMessage().equals("1")) {
                            System.out.println("Error with Noughts and Crosses program: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;
                
                // Saskia
				// Runs the Master Mind program when option 6 is selected
				case "6":
                    System.out.println("\nStarting Master Mind...\n");
                    try {
                        // Call the Start method for the Master Mind program when implemented
                    } catch (Exception e) {
                        //uses errors that throw "1" as a replacement for System.exit so it returns to the main menu
                        if (!e.getMessage().equals("1")) {
                            System.out.println("Error with Master Mind program: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;

                // Arkin
				// Runs the Draw a Shape program when option 7 is selected
				case "7":
                    System.out.println("\nStarting Draw a Shape...\n");
                    try {
                        // Call the Start method for the Draw a Shape program when implemented
                    } catch (Exception e) {
                        System.out.println("Error with Draw a Shape program: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                // Naimul
				// Runs the ZigZag program when option 8 is selected
				case "8":
                    System.out.println("\nStarting ZigZag...\n");
                    try {
                        // Call the Start method for the ZigZag program when implemented
                    } catch (Exception e) {
                        System.out.println("Error with ZigZag program: " + e.getMessage());
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
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option. Please choose a number from 1 to 10.");
            }
        }

        scanner.close();
	}

}