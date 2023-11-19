public class Validators {

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

    public static boolean isStringLongEnough(String str, int requiredLength) {
        return requiredLength >= str.length();
    }

    public static boolean isDigitInString(String str) {
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

    public static boolean isLowercaseLetterInString(String str) {
        for (Character character : str.toCharArray()) {
            if (Character.isLowerCase(character)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSpaceInString(String str) {
        for (String letter : str.split("")) {
            if (letter.equals(" ")) {
                return true;
            }
        }
        return false;
    }

}
