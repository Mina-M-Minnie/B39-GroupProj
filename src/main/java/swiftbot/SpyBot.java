package swiftbot;

import swiftbot.SwiftBotAPI;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Scanner;


public class SpyBot {
	
	private final SwiftBotAPI swiftBot;
    private final ButtonInputHandler buttonInputHandler;
    private final UserAuthentication userAuthentication;
    
	public SpyBot() {
		swiftBot = SwiftBotAPI.INSTANCE;
        buttonInputHandler = new ButtonInputHandler(swiftBot);
        userAuthentication = new UserAuthentication();
	}

	public void start() {
		//Display Welcome Message
		System.out.println(
					"============================================================\n" +
					"              ███████╗██████╗ ██╗   ██╗\n" +
					"              ██╔════╝██╔══██╗╚██╗ ██╔╝\n" +
					"              ███████╗██████╔╝ ╚████╔╝\n" +
					"              ╚════██║██╔═══╝   ╚██╔╝\n" +
					"              ███████║██║        ██║\n" +
					"              ╚══════╝╚═╝        ╚═╝\n" +
					"\n" +
					"                    SPYBOT v1.0\n" +
					"============================================================\n" +
					"Secure Morse Communication on SwiftBot\n" +
					"\n" +
					"Controls:\n" +
					"- QR Scan: SwiftBot Camera\n" +
					"- Morse Input: SwiftBot Buttons\n" +
					"- Quit (any safe screen): Press X button when prompted\n" +
					"\n" +
					"Press any key in the terminal to begin...\n"
				);
		
		// Scanner
		Scanner input = new Scanner(System.in);
		
		// Wait for user to press Enter before continuing
		input.nextLine();
		
		//User Authentication
		boolean userVerified = false;
	
		//Loop the Authentication Process until authentication is success or user terminates the program.
		while(!userVerified) {
			//Sender Authentication Screen
		    System.out.println(
		            "------------------------ SENDER AUTH ---------------------------\n" +
		            "Step 1/7: Sender verification required.\n" +
		            "\n" +
		            "Please present your QR code to the SwiftBot camera.\n" +
		            "Expected format: <CALLSIGN>:<LOCATION>\n" +
		            "Example: EAGLE7:A\n" +
		            "\n" +
		            "[Scanning...]\n" +
		            "Status: WAITING_FOR_QR\n" +
		            "-----------------------------------------------------------------"
		        );

		    //Capture the QR image from the camera
		    String QRCode = scanQRWithTimeout(5, 2000);		// 5 attempts x 2 seconds each
			
			//Verify the Users QR Code
			if(userAuthentication.Authenticate(QRCode) == true) {
				//User Auth Success Screen
			    System.out.println(
			            "[Scanning...]\n" +
			            "QR Detected: \"" + QRCode + "\"\n" +
			            "Validating format... OK\n" +
			            "Checking agent registry... FOUND\n" +
			            "\n" +
			            "✅ AUTH SUCCESS\n" +
			            "Sender: " + userAuthentication.getCallsign() + "\n" +
			            "Location: " + userAuthentication.getLocation() + "\n" +
			            "\n" +
			            "Press ENTER to continue to message recording...\n" +
			            "> "
			        );

			        input.nextLine(); // wait for Enter
				
				userVerified = true;
			} else {
				//User QR Verification fails
				
				//Pick correct error screen based on why Authentication has failed
				switch(userAuthentication.getLastFailReason()) {
					case NO_QR:
			            // No QR Detected Screen
			            System.out.println(
			                "[Scanning...]\n" +
			                "❌ ERROR: No QR code detected.\n" +
			                "Tips:\n" +
			                "- Hold the QR code steady (2-3 seconds)\n" +
			                "- Improve lighting / reduce glare\n" +
			                "- Ensure the full QR code is visible in frame\n" +
			                "\n" +
			                "Retry Scan? (Y/N)\n" +
			                "> "
			            );
			            break;

			        case INVALID_FORMAT:
			            // Invalid QR Format Screen
			            System.out.println(
			                "QR Detected: \"" + QRCode + "\"\n" +
			                "❌ ERROR: Invalid QR format.\n" +
			                "Required format: <CALLSIGN>:<LOCATION>\n" +
			                "Examples: ALPHA1:A    FOX99:C\n" +
			                "\n" +
			                "Retry Scan? (Y/N)\n" +
			                "> "
			            );
			            break;

			        case UNKNOWN_AGENT:
			            // Unknown Agent / Invalid Location Screen
			            System.out.println(
			                "QR Detected: \"" + QRCode + "\"\n" +
			                "Format... OK\n" +
			                "❌ ERROR: Agent not recognised OR invalid location.\n" +
			                "Valid locations: A, B, C\n" +
			                "Callsign must match a registered agent.\n" +
			                "\n" +
			                "Retry Scan? (Y/N)\n" +
			                "> "
			            );
			            break;
					
				}
				
				//Ask user if they want to try again
				String answer;

				//Take users inputs until a valid input is given "Y" or "N"
				do {
				    answer = input.nextLine().toUpperCase();
				} while (!answer.equals("Y") && !answer.equals("N"));
				
				// If user enters "N" Terminate the program
				if(answer.equals("N")) {
					shutdown();
				}
			}
		}
		
		//Display Instructions Screen
		System.out.println(
			    "-------------------- MORSE INPUT GUIDE -------------------------\n" +
			    "Step 2/7: Record message in Morse using SwiftBot buttons.\n" +
			    "\n" +
			    "Button mapping:\n" +
			    "  X = DOT (.)\n" +
			    "  Y = DASH (-)\n" +
			    "  A = END OF CHARACTER\n" +
			    "  B = END OF WORD\n" +
			    "  0 (Morse digit) = END OF MESSAGE\n" +
			    "\n" +
			    "Message format rule:\n" +
			    "  Destination first, then message\n" +
			    "  e.g., B <end word> WANT <end word> GEAR <end message>\n" +
			    "\n" +
			    "Press ENTER when ready...\n" +
			    "> "
			);
		
		input.nextLine(); // wait for Enter
		System.out.println("-----------------------------------------------------------------");
		
		
		//Record Message
		//Capture timestamp just before recording starts
		LocalDateTime timeRecorded = LocalDateTime.now();
		
		RecordMorseMessage recorder = new RecordMorseMessage(buttonInputHandler, input);
		String message = recorder.recordMorseMessage();
		
		// If the user chose not to re-record, shut down cleanly
		if(message == null) {
			shutdown();
		}
		
	
        //Extract destination from the recorded morse
        //The first morse word before the first "/" is the destination
        //Decode it using MorseCodeTranslator to get A, B or C
        MorseCodeTranslator translator = new MorseCodeTranslator();
        String destinationMorse = message.split("/")[0].trim();

        // Remove any trailing | from the destination morse symbol
        // e.g. "-...|" -> "-.."
        String cleanDestMorse = destinationMorse.replace("|", "");
        String destination = translator.isValid(cleanDestMorse)
                ? translator.translate(cleanDestMorse)
                : "B"; // fallback - should never happen after validation

        String senderLocation  = userAuthentication.getLocation();
        String senderCallsign  = userAuthentication.getCallsign();
		
		//Priority Level
		PrioritySelection prioritySelector = new PrioritySelection(buttonInputHandler, input);
		PrioritySelection.Priority priority = prioritySelector.selectPriority();
		
        // Navigate to Destination
        NavigateToDestination navigator = new NavigateToDestination(swiftBot, input);
        navigator.navigate(senderLocation, destination, priority);
		
		//Authenticate Receiver
		boolean receiverVerified = false;
		String receiverCallsign = "";
        String receiverLocation = "";
		
		while(!receiverVerified) {
			// Receiver Auth Screen
			System.out.println(
			        "-------------------- RECEIVER AUTH -----------------------------\n" +
			        "Receiver must scan QR code to receive message.\n" +
			        "\n" +
			        "Expected receiver location: " + destination + "\n" +
			        "Expected format: <CALLSIGN>:<LOCATION>\n" +
			        "\n" +
			        "[Scanning...]\n" +
			        "Status: WAITING_FOR_QR\n" +
			        "-----------------------------------------------------------------"
			    );
			
			//Capture the QR image from the camera
			String receiverQR = scanQRWithTimeout(5, 2000);		// 5 attempts x 2 seconds each

	            if (userAuthentication.AuthenticateReciever(receiverQR, destination)) {

	                // Store receiver details for logging
	                receiverCallsign = userAuthentication.getCallsign();
	                receiverLocation = userAuthentication.getLocation();
	                receiverVerified = true;

	                // UI-08A: Receiver Verified
	                System.out.println(
	                    "QR Detected: \"" + receiverQR + "\"\n" +
	                    "Validating... OK\n" +
	                    "✅ RECEIVER VERIFIED\n" +
	                    "\n" +
	                    "Press any SwiftBot button when ready to view the message...\n" +
	                    "-----------------------------------------------------------------"
	                );

	                // Wait for any button press before delivering
	                buttonInputHandler.waitForPress();

	            } else {

	                switch (userAuthentication.getLastFailReason()) {

	                    case NO_QR:
	                        System.out.println(
	                            "[Scanning...]\n" +
	                            "❌ ERROR: No QR code detected.\n" +
	                            "Tips:\n" +
	                            "- Hold the QR code steady (2-3 seconds)\n" +
	                            "- Improve lighting / reduce glare\n" +
	                            "- Ensure the full QR code is visible in frame\n" +
	                            "\n" +
	                            "Retry scan? (Y/N)\n> "
	                        );
	                        String ans1;
	                        do { ans1 = input.nextLine().toUpperCase(); }
	                        while (!ans1.equals("Y") && !ans1.equals("N"));
	                        if (ans1.equals("N")) { navigator.navigate(destination, senderLocation, priority); shutdown(); }
	                        break;

	                    case INVALID_FORMAT:
	                        System.out.println(
	                            "QR Detected: \"" + receiverQR + "\"\n" +
	                            "❌ ERROR: Invalid QR format.\n" +
	                            "Required format: <CALLSIGN>:<LOCATION>\n" +
	                            "\n" +
	                            "Retry scan? (Y/N)\n> "
	                        );
	                        String ans2;
	                        do { ans2 = input.nextLine().toUpperCase(); }
	                        while (!ans2.equals("Y") && !ans2.equals("N"));
	                        if (ans2.equals("N")) { navigator.navigate(destination, senderLocation, priority); shutdown(); }
	                        break;

	                    case UNKNOWN_AGENT:
	                        System.out.println(
	                            "QR Detected: \"" + receiverQR + "\"\n" +
	                            "❌ ERROR: Receiver callsign not found in registry.\n" +
	                            "\n" +
	                            "Retry scan? (Y/N)\n> "
	                        );
	                        String ans3;
	                        do { ans3 = input.nextLine().toUpperCase(); }
	                        while (!ans3.equals("Y") && !ans3.equals("N"));
	                        if (ans3.equals("N")) { navigator.navigate(destination, senderLocation, priority); shutdown(); }
	                        break;

	                    case LOCATION_MISMATCH:
	                        System.out.println(
	                            "QR Detected: \"" + receiverQR + "\"\n" +
	                            "❌ ERROR: Receiver location does not match destination.\n" +
	                            "Destination required: " + destination + "\n" +
	                            "Scanned: " + userAuthentication.getLocation() + "\n" +
	                            "\n" +
	                            "Options:\n" +
	                            "1) Retry scan\n" +
	                            "2) Deny delivery and return to sender\n" +
	                            "\n" +
	                            "Choose (1/2):\n> "
	                        );
	                        String choice;
	                        do { choice = input.nextLine().trim(); }
	                        while (!choice.equals("1") && !choice.equals("2"));
	                        if (choice.equals("2")) { navigator.navigate(destination, senderLocation, priority); shutdown(); }
	                        break;

	                    default:
	                        break;
	                }
	            }
	        }
		
		//Deliver Message
		LocalDateTime timeDelivered = LocalDateTime.now();
	
	    DeliverMessage delivery = new DeliverMessage(swiftBot, input);
	    try {
	        delivery.deliver(message, senderCallsign, senderLocation);
	    } catch (InterruptedException e) {
	        System.out.println("❌ Delivery interrupted: " + e.getMessage());
	    }
		
		//Create a Log
	    String[] morseParts   = message.split("/", 2);
        String payloadMorse   = morseParts.length > 1 ? morseParts[1].trim() : "";
        StringBuilder decoded = new StringBuilder();

        for (String word : payloadMorse.split("/")) {
            for (String sym : word.trim().split("\\|")) {
                sym = sym.trim();
                if (translator.isValid(sym)) decoded.append(translator.translate(sym));
            }
            decoded.append(" ");
        }
        String decodedMessage = decoded.toString().trim();

        LogMessage logger = new LogMessage(input);
        logger.log(
            senderCallsign,
            senderLocation,
            receiverCallsign,
            receiverLocation,
            destination,
            decodedMessage,
            priority,
            timeRecorded,
            timeDelivered
        );
	    
		//Return the bot to the sender
        System.out.println(
                "----------------------- RETURN JOURNEY -------------------------\n" +
                "Returning SwiftBot to sender location: " + senderLocation + "\n" +
                "\n" +
                "Status: NAVIGATING\n" +
                "-----------------------------------------------------------------"
            );

            navigator.navigate(destination, senderLocation, priority);

            System.out.println(
                "✅ SwiftBot returned to sender location: " + senderLocation + "\n" +
                "\n" +
                "Mission complete. Shutting down...\n"
            );

            shutdown();
        }
	
	private String scanQRWithTimeout(int maxAttempts, int delayMs) {
	    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
	        System.out.println("Scanning... attempt " + attempt + "/" + maxAttempts);

	        BufferedImage qrImg = swiftBot.getQRImage();
	        String result = swiftBot.decodeQRImage(qrImg);

	        if (result != null && !result.isEmpty()) {
	            return result; // QR detected — return immediately
	        }

	        try {
	            Thread.sleep(delayMs); // wait before trying again
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	    }
	    return ""; // nothing detected after all attempts
	}
	
	
	public void shutdown() {
		//Shutting Down Screen
		System.out.println(
			       "\n" +
			       "============================================================\n" +
			       "                    SHUTTING DOWN\n" +
			       "============================================================\n" +
			       "\n" +
			       "  Securing communication channels...\n" +
			       "  Clearing sensitive data...\n" +
			       "  Disabling SwiftBot systems...\n" +
			       "\n" +
			       "============================================================\n" +
			       "              ███████╗██████╗ ██╗   ██╗\n" +
			       "              ██╔════╝██╔══██╗╚██╗ ██╔╝\n" +
			       "              ███████╗██████╔╝ ╚████╔╝\n" +
			       "              ╚════██║██╔═══╝   ╚██╔╝\n" +
			       "              ███████║██║        ██║\n" +
			       "              ╚══════╝╚═╝        ╚═╝\n" +
			       "\n" +
			       "  SPYBOT v1.0 -- SESSION TERMINATED\n" +
			       "  All systems offline. Goodbye, Agent.\n" +
			       "\n" +
			        "============================================================\n"
			   );
		 
		 // Clean up SwiftBot hardware
		 swiftBot.disableUnderlights();
		 swiftBot.disableAllButtons();
		 swiftBot.disableButtonLights();
		 swiftBot.stopMove();
		    
		System.exit(0);		// Terminates the program
	}

}
