package SpyBot;

import java.util.HashMap;
import java.util.Map;

public class MorseCodeTranslator {

	// Creates a HashMap that maps morse symbols to a letter
    private static final Map<String, String> morseToChar = new HashMap<>();

    // Filling the HashMap table
    static {
        morseToChar.put(".-",   "A"); morseToChar.put("-...", "B");
        morseToChar.put("-.-.", "C"); morseToChar.put("-..",  "D");
        morseToChar.put(".",    "E"); morseToChar.put("..-.", "F");
        morseToChar.put("--.",  "G"); morseToChar.put("....", "H");
        morseToChar.put("..",   "I"); morseToChar.put(".---", "J");
        morseToChar.put("-.-",  "K"); morseToChar.put(".-..", "L");
        morseToChar.put("--",   "M"); morseToChar.put("-.",   "N");
        morseToChar.put("---",  "O"); morseToChar.put(".--.", "P");
        morseToChar.put("--.-", "Q"); morseToChar.put(".-.",  "R");
        morseToChar.put("...",  "S"); morseToChar.put("-",    "T");
        morseToChar.put("..-",  "U"); morseToChar.put("...-", "V");
        morseToChar.put(".--",  "W"); morseToChar.put("-..-", "X");
        morseToChar.put("-.--", "Y"); morseToChar.put("--..", "Z");
        morseToChar.put("-----","0"); morseToChar.put(".----","1");
        morseToChar.put("..---","2"); morseToChar.put("...--","3");
        morseToChar.put("....-","4"); morseToChar.put(".....","5");
        morseToChar.put("-....","6"); morseToChar.put("--...","7");
        morseToChar.put("---..","8"); morseToChar.put("----.","9");
    }

    // Translate a single morse symbol (e.g. ".-") to a character
    public String translate(String morseSymbol) {
        return morseToChar.getOrDefault(morseSymbol, null);
    }

    // Check if a morse symbol is valid
    public boolean isValid(String morseSymbol) {
        return morseToChar.containsKey(morseSymbol);
    }
}