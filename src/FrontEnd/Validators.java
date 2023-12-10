package FrontEnd;

import java.text.DecimalFormat;

public class Validators {
    public static boolean isValidName(String name) {
        if (name.matches("[a-zA-Z]+")) {
            return true;
        } else if (isSpaceInString(name)) {
            ConsolePrinter.printError("The name [" + name + "] cannot contain spaces!");
            return false;
        }
        ConsolePrinter.printError("The name [" + name + "] should only contain letters [a-z] or [A-Z].");
        return false;
    }

    public static boolean isValidUsername(String username, int requiredLength) {
        // Check if the username contains only letters, digits, and underscores
        if (!isStringLongEnough(username, requiredLength)) {
            ConsolePrinter.printError("The username [" + username + "] needs to be at least [" + requiredLength + "] symbols long!");
            return false;
        } else if (isSpaceInString(username)) {
            ConsolePrinter.printError("The username [" + username + "] cannot contain spaces!");
            return false;
        } else if (username.matches("[a-zA-Z0-9_]+")) {
            return true;
        }
        ConsolePrinter.printError("The username [" + username + "] can only contain letters [a-z], digits [0-9] or underscores [_]");
        return false;
    }

    public static boolean isPasswordValid(String password, int requiredLength) {
        if (isSpaceInString(password)) {
            ConsolePrinter.printError("The password cannot contain spaces!");
            return false;
        } else if (!isStringLongEnough(password, requiredLength)) {
            ConsolePrinter.printError("The password needs to be at least [" + requiredLength + "] symbols long!");
            return false;
        } else if (!isDigitInString(password)) {
            ConsolePrinter.printError("The password should have at least one digit!");
            return false;
        } else if (!isCapitalLetterInString(password)) {
            ConsolePrinter.printError("The password should have at least one capital letter!");
            return false;
        } else if (!isLowercaseLetterInString(password)) {
            ConsolePrinter.printError("The password should have at least one lowercase letter!");
            return false;
        }
        return true;
    }

    private static boolean isStringLongEnough(String str, int requiredLength) {
        return str.length() >= requiredLength;
    }

    private static boolean isDigitInString(String str) {
        for (Character character : str.toCharArray()) {
            if (Character.isDigit(character)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCapitalLetterInString(String str) {
        for (Character character : str.toCharArray()) {
            if (Character.isUpperCase(character)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLowercaseLetterInString(String str) {
        for (Character character : str.toCharArray()) {
            if (Character.isLowerCase(character)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSpaceInString(String str) {
        for (String letter : str.split("")) {
            if (letter.equals(" ")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFirstLetterCapital(String str) {
        char firstChar = str.charAt(0);
        return Character.isUpperCase(firstChar);
    }

    private static boolean areAllLettersStartingAtSecondLowercase(String str) {
        for (int i = 1; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNameWrittenCorrectly(String name) {
        // Capitalized + all other letters lowercase
        return isFirstLetterCapital(name) && areAllLettersStartingAtSecondLowercase(name);
    }

    static boolean isNumberInArray(int[] array, int targetNumber) {
        for (int number : array) {
            if (number == targetNumber) {
                return true;
            }
        }
        return false;
    }

    static String intArrayToString(int[] array) {
        String[] stringArray = new String[array.length];

        for (int i = 0; i < array.length; i++) {
            stringArray[i] = String.valueOf(array[i]);
        }

        return String.join(", ", stringArray);
    }

    static boolean isInteger(String integerToTest) {
        try {
            Integer.parseInt(integerToTest);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatDecimalNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(number);
    }
}
