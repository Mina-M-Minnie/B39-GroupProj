package swiftbot.SpyBot;

import swiftbot.Button;
import swiftbot.SwiftBotAPI;

public class ButtonInputHandler {

	private final SwiftBotAPI swiftBot;
    private MorseCodeInputs lastElementPressed;

    public ButtonInputHandler(SwiftBotAPI swiftBot) {
        this.swiftBot = swiftBot;
        this.lastElementPressed = null;

        //disble all buttons initially
        swiftBot.disableAllButtons();

        enableButtons();
    }

    private void enableButtons() {

        // A -> END_OF_CHAR
        swiftBot.enableButton(Button.A, () -> {
        	lastElementPressed = MorseCodeInputs.END_OF_CHAR;
            //System.out.println("Button A (END OF CHARACTER) pressed");
        });

        // B -> END_OF_WORD
        swiftBot.enableButton(Button.B, () -> {
        	lastElementPressed = MorseCodeInputs.END_OF_WORD;
            //System.out.println("Button B (END OF WORD) pressed");
        });

        // X -> DOT
        swiftBot.enableButton(Button.X, () -> {
        	lastElementPressed = MorseCodeInputs.DOT;
            //System.out.println("Button X (DOT) pressed");
        });

        // Y -> DASH
        swiftBot.enableButton(Button.Y, () -> {
        	lastElementPressed = MorseCodeInputs.DASH;
            //System.out.println("Button Y (DASH) pressed");
        });
    }

    public MorseCodeInputs waitForPress() {
    	lastElementPressed = null;

        while (lastElementPressed == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.err.println("LED sleep interrupted: " + e.getMessage());
            }
        }
        
        return lastElementPressed;
    }

}
