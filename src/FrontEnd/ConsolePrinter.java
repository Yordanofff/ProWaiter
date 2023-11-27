package FrontEnd;

public class ConsolePrinter {
    // ANSI escape codes for text formatting
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // todo - don't want to add the STATUS_COLOR options in the constructor, but I want to be able to change them globally.
    //  Might move them to a config file later on.
    public static final String STATUS_COLOR = MAGENTA;
    public static final boolean DIFFERENT_STATUS_COLOR = false;

    /*
    This method will print an Error Message but will place every part of it that is inside square
    brackets in yellow. It will print the whole message in RED if no brackets used.
     */
    public static void printError(String message) {
        System.out.println(getColoredMsgInBracketsWithStatus("error", message, RED, YELLOW));
    }

    public static void printWarning(String message) {
        System.out.println(getColoredMsgInBracketsWithStatus("warning", message, YELLOW, RED));
    }

    public static void printInfo(String message) {
        System.out.println(BLUE + "[INFO] " + message + RESET);
    }

    public static void printQuestion(String message) {
        System.out.println(getColoredMsgInBrackets(message, BLUE, YELLOW));
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "[SUCCESS] " + message + RESET);
    }

    public static void printCustomColor(String message, String color) {
        System.out.println(color + message + RESET);
    }

    private static StringBuilder getColoredMsgInBracketsWithStatus(String status, String message, String mainColor, String bracketsColor) {
        if (DIFFERENT_STATUS_COLOR) {
            return getColoredMsgInBracketsWithStatusDifferentColor(status, message, mainColor, bracketsColor);
        } else {
            return getColoredMsgInBracketsWithStatusSameAsMainColor(status, message, mainColor, bracketsColor);
        }
    }

    private static StringBuilder getColoredMsgInBracketsWithStatusSameAsMainColor(String status, String message, String mainColor, String bracketsColor) {
        StringBuilder statusMsg = getColoredMsgInBrackets("[" + status.toUpperCase() + "] ", mainColor);
        StringBuilder actualMsg = getColoredMsgInBrackets(message, mainColor, bracketsColor);
        return statusMsg.append(actualMsg);
    }

    private static StringBuilder getColoredMsgInBracketsWithStatusDifferentColor(String status, String message, String mainColor, String bracketsColor) {
        StringBuilder statusMsg = getColoredMsgInBrackets("[" + status.toUpperCase() + "] ", STATUS_COLOR);
        StringBuilder actualMsg = getColoredMsgInBrackets(message, mainColor, bracketsColor);
        return statusMsg.append(actualMsg);
    }

    private static StringBuilder getColoredMsgInBrackets(String message, String mainColor, String bracketsColor) {
        StringBuilder result = new StringBuilder();
        boolean insideBrackets = false;
        boolean endingBracketReached = false;

        for (char c : message.toCharArray()) {
            if (c == '[') {
                result.append(bracketsColor);
                insideBrackets = true;
            } else if (c == ']') {
                endingBracketReached = true;
                insideBrackets = false;
                result.append(c);
                result.append(RESET);
            }

            if (!insideBrackets) {
                result.append(mainColor);
            }

            // Don't print closing bracket twice if it's already been printed.
            if (!endingBracketReached) {
                result.append(c);
            } else {
                endingBracketReached = false;
            }

        }

        result.append(RESET);
        return result;
    }

    private static StringBuilder getColoredMsgInBrackets(String message, String mainColor) {
        return getColoredMsgInBrackets(message, mainColor, mainColor);
    }

    private static String getColoredMsg(String msg, String ansiColor) {
        return ansiColor + msg + RESET;
    }

    public static String getGreenMsg(String msg) {
        return getColoredMsg(msg, GREEN);
    }
}