public class BracketColorPrinter {

    public static void main(String[] args) {
        String x = "This is a msg [xxx] some more text [yyy] more more234 [ this text is not in brackets";
        printColoredMessage(x);
    }

//    private static void printColoredMessage(String input) {
//        String[] parts = splitBetweenBrackets(input);
//
//        for (String part : parts) {
//            if (part.startsWith("[") && part.endsWith("]")) {
//                System.out.print(ConsolePrinter.YELLOW + part + ConsolePrinter.RESET);
//            } else {
//                System.out.print(ConsolePrinter.RED + part + ConsolePrinter.RESET);
//            }
//        }
//        System.out.println();
//    }

    private static void printColoredMessage(String input) {
        StringBuilder result = new StringBuilder();
        boolean insideBrackets = false;

        for (char c : input.toCharArray()) {
            if (c == '[') {
                result.append(ConsolePrinter.YELLOW);
                insideBrackets = true;
            } else if (c == ']') {
                result.append(ConsolePrinter.RESET);
                insideBrackets = false;
            }

            if (!insideBrackets) {
                result.append(ConsolePrinter.RED);
            }

            result.append(c);
        }

        result.append(ConsolePrinter.RESET);
        System.out.println(result);
    }

    private static String[] splitBetweenBrackets(String input) {
        return input.split("(\\[.*?\\])");
    }
}