package swiftbot;

public class UserAuthentication {
		
	/*
	 * USER QR CODES
	 * ALPHA1:A
	 * BRAVO2:B
	 * CHARLIE3:C
	 */
	
	// Variables
	private String Callsign;
	private String Location;
	
	public enum AuthFailReason { NO_QR, INVALID_FORMAT, UNKNOWN_AGENT, LOCATION_MISMATCH }		// All possible ways Authentication can Fail
	private AuthFailReason lastFailReason;

	//Returns the Reason why a QR Authentication Scan has failed
	public AuthFailReason getLastFailReason() {
		return lastFailReason;
	}
	
	//Authenticate the User
	public boolean Authenticate(String QRCode) {
		
		//Ensures QR code is not empty
		if (QRCode == null || QRCode.isEmpty()) {
			lastFailReason = AuthFailReason.NO_QR;
			return false;
		}
		
		//Extract the Callsign and Location from the QR code
		String[] parts = QRCode.split(":");
		
		// Check QR format is correct
		if (parts.length != 2) {
			lastFailReason = AuthFailReason.INVALID_FORMAT;
			return false;
		}

		Callsign = parts[0];
		Location = parts[1];
		
		//Check correct Syntax is used
		if(isValidCallSign(Callsign) == false || isValidLocation(Location) == false) {
			lastFailReason = AuthFailReason.INVALID_FORMAT;
			return false;
		}
		
		//Check if user exists
		if(!userExists(Callsign, Location)) {
			lastFailReason = AuthFailReason.UNKNOWN_AGENT;
			return false;
		}
		
		return true;
	}
	
	// Checks everything the normal Authenticate() does plus verified location matches destination
	public boolean AuthenticateReciever(String QRCode, String expectedDestination) {
		// Running the standard authentication first
		if(!Authenticate(QRCode)) {
			return false;
		}
		
		//Extra check: location must match the expected location
		if(!Location.equals(expectedDestination) ) {
			lastFailReason = AuthFailReason.LOCATION_MISMATCH;
			return false;
		}
		
		return true;
	}
	
	//Check if Correct Syntax is used for Call Sign
	private boolean isValidCallSign(String callSign) {
		//Callsign must be a string
		if(callSign == null || callSign.isEmpty()) return false;
		
		//call sign must not contain any spaces
		if(callSign.contains(" ")) return false;
		
		return callSign.matches("[A-Za-z0-9]+");
	}
	
	
	//Check if Correct Syntax is used for the Location
	private boolean isValidLocation(String location) {
		//Location must only be a single charcter
		if(location == null || location.length() != 1) return false;
		
		//call Location must not contain any spaces
		if(location.contains(" ")) return false;
		
		//Check location has no numbers
		if(location.matches("[0-9]")) return false;
		
		return true;
	}
	
	//Check if User Exists
	private boolean userExists(String callSign, String location) {

	    return (callSign.equals("ALPHA1") && location.equals("A")) ||
	           (callSign.equals("BRAVO2") && location.equals("B")) ||
	           (callSign.equals("CHARLIE3") && location.equals("C"));
	}
	
	public String getCallsign() {
		return Callsign;
	}
	
	public String getLocation() {
		return Location;
	}

}
