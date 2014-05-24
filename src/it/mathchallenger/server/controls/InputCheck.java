package it.mathchallenger.server.controls;

import java.util.regex.Pattern;

public class InputCheck {
	private final static int USERNAME_MAX_LENGTH = 30;

	public static boolean isValidUsername(String n) {
		if (n.length() > USERNAME_MAX_LENGTH)
			return false;
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]{1,30}[._]{0,1}[a-zA-Z0-9]{1,29}");
		return true;
	}

}
