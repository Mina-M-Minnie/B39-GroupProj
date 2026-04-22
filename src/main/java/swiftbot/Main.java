package swiftbot;

import java.io.PrintStream;

public class Main {

	public static void main(String[] args) throws Exception {
		System.setOut(new PrintStream(System.out, true, "UTF-8"));
		 
		// Starts the SpyBot Software when run
		SpyBot program = new SpyBot();
		program.start();
	}

}