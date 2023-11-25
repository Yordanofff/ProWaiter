package FrontEnd;

import BackEnd.Users.User;
import BackEnd.Users.UserManager;
import BackEnd.Users.UserType;

import java.util.*;

public class MenuBuilder {
    static final int MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU = 5;
    static final String MENU_SEPARATOR = " - ";  // With spaces if required.
    static final String MENU_LINE_SYMBOL = "-";
    static Scanner scanner = new Scanner(System.in);

    public static void buildLoginMenu() {
        String[] menuOptions = new String[]{"Login"};
        String topMenuLabel = "Please Login with your credentials:";
        String optionZeroText = "Exit";
        String optionZeroMsg = "GoodBye!";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, MenuBuilder::LoginMenuAction);
    }

    public static void buildAdminMenu() {
        String[] menuOptions = new String[]{"User Management", "Menu Management", "Order management"};
        String topMenuLabel = "Hello admin!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, MenuBuilder::AdminMenuAction);
    }

    public static void buildUserManagementMenu() {
        String[] menuOptions = new String[]{"View all users", "Add user", "Edit user", "Delete user"};
        String topMenuLabel = "Hello admin!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, MenuBuilder::AdminMenuAction);
    }


    public static void buildMenu(String[] menuOptions, String topMenuLabel, String optionZeroText, String optionZeroMsg, MenuAction menuAction) {
        while (true) {
            int selectedOption = printMenuAndGetUsersChoice(menuOptions, topMenuLabel, optionZeroText);

            // Exit if 0
            if (selectedOption == 0) {
                System.out.println(optionZeroMsg);
                break;
            }

            // Executing the selected option
//            AdminMenuAction(selectedOption);
            menuAction.execute(selectedOption);

            // pause
            System.out.print("Press any key to continue..: ");
            scanner.nextLine();

            System.out.println();
        }
        // todo - another parameter to
    }

    // todo
    public static void LoginMenuAction(int option) {
        // Call methods to run
        switch (option) {
            case 1 -> UserLoginMenuAction();
        }
    }

    public static void UserLoginMenuAction() {
        User user = UserManager.getTheLoginUserIfUsernameAndPasswordAreCorrect();
        if (user != null) {
            System.out.println("Hello " + user.getFullName());
            // todo - create the Menus for the different types Admin Menu / Cook Menu / Waiter Menu
            if (user.getUserType() == UserType.ADMIN) {
                System.out.println("Opening Admin panel");
                buildAdminMenu();
            } else if (user.getUserType() == UserType.KITCHEN) {
                System.out.println("Opening Kitchen panel");
            } else if (user.getUserType() == UserType.WAITER){
                System.out.println("Opening Waiter panel");
            }
        }
    }

    public static void AdminMenuAction(int option) {
        // Call methods to run
        switch (option) {
            case 1 -> System.out.println("Admin Action 1");
            case 2 -> System.out.println("Admin Action 2");
            case 3 -> System.out.println("Admin Action 3");
        }
    }

    private static int printMenuAndGetUsersChoice(String[] menuOptions, String topMenuQuestion, String optionZeroText) {
        // Creating HashMap with numbers and options. + Adding Exit/Logout/Go Back
        HashMap<Integer, String> menuOptionsWithNumbers = generateHashMapMenuOptionsWithNumbers(menuOptions, optionZeroText);

        // Fixing spaces before the numbers, adding separator and storing all in a list.
        List<String> dataToPrint = getMenuOptionsWithSameLengthOfMaxDigitLength(menuOptionsWithNumbers);

        // Printing the options in a frame
        printMenuOptionsInFrame(topMenuQuestion, dataToPrint);

        // Returning the user selection
        return getUserInputFrom0toNumber(menuOptions.length);
    }

    private static int getUserInputFrom0toNumber(int numOptions) {
        int choice;

        while (true) {
            String ans = scanner.nextLine().strip();

            try {
                choice = Integer.parseInt(ans);

                if (choice >= 0 && choice <= numOptions) {
                    break;
                } else {
                    ConsolePrinter.printError("Please enter a number between [0 - " + numOptions + "]");
                }
            } catch (NumberFormatException e) {
                ConsolePrinter.printError("Invalid input [" + ans + "]! " +
                        "Please enter an integer in the range [0 - " + numOptions + "]");
            }
        }

        return choice;
    }

    private static HashMap<Integer, String> generateHashMapMenuOptionsWithNumbers(String[] menuOptionsWithoutExit, String optionZeroText) {
        // Will add Exit as position 0.

        // Using LinkedHashMap to keep the order as they were added.
        HashMap<Integer, String> menuOptionsWithNumbers = new LinkedHashMap<>();
        menuOptionsWithNumbers.put(0, optionZeroText);  // Exit/LogOut/Go Back

        for (int i = 1; i <= menuOptionsWithoutExit.length; i++) {
            menuOptionsWithNumbers.put(i, menuOptionsWithoutExit[i - 1]);
        }

        return menuOptionsWithNumbers;
    }

    /**
     * This method will add trailing spaces in front of the numbers so that they will be printed one below another.
     * Also adds separator between the number and the option name.
     * " 7 - Option ..."
     * " 8 - Option ..."
     * " 9 - Option ..."
     * "10 - Option ..."
     * "11 - Option ..."
     *
     * @param hashMapMenuOptionsWithNumbers - HashMap of (number, option name)
     * @return List<String> with all options
     */
    private static List<String> getMenuOptionsWithSameLengthOfMaxDigitLength(HashMap<Integer, String> hashMapMenuOptionsWithNumbers) {
        List<String> dataToPrint = new ArrayList<>();

        int longestKey = getNumberOfSymbolsInSetOfIntegers(hashMapMenuOptionsWithNumbers.keySet());
        String keyToString;
        for (int key : hashMapMenuOptionsWithNumbers.keySet()) {
            String value = String.valueOf(hashMapMenuOptionsWithNumbers.get(key));

            // Convert int to String, to be able to add trailing spaces.
            keyToString = String.valueOf(key);
            // Format string doesn't work because will be printed normally not pritf...
            keyToString = addTrailingSpacesBeginningOfString(keyToString, longestKey);

            dataToPrint.add(keyToString + ConsolePrinter.getGreenMsg(MENU_SEPARATOR) + value);
        }

        return dataToPrint;
    }


    /**
     * This method will be used to print the menu options. Adds a frame that will be surrounded
     * by at least MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU spaces (for the longest word) and more for the rest.
     * ===============================================
     * |     menuTopQuestion                         |
     * ===============================================
     * |     1 - menuOptions 1                       |
     * |     2 - menuOptions 2                       |
     * |     3 - menuOptions 3 ...                   |
     * ===============================================
     *
     * @param menuTopQuestion - Top menu question
     * @param menuOptions     - List of options
     */
    public static void printMenuOptionsInFrame(String menuTopQuestion, List<String> menuOptions) {

        int longestRowWithData = getTheNumberOfSymbolsInTheLongestString(menuTopQuestion, menuOptions);

        // Add the minimum spaces on both sides of the string + 2 symbols for the frame (left + right side)
        int frameLength = longestRowWithData + (MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU * 2) + 2;

        printHorizontalLine(frameLength);
        printMiddleMenuLine(frameLength, menuTopQuestion);
        printHorizontalLine(frameLength);

        // The first element will be [0 - Exit] or [0 - Log Out] etc.. Don't print it in the top part.
        boolean isZeroElement = true;
        for (String menuOptionRow : menuOptions) {
            if (isZeroElement) {
                isZeroElement = false;
                continue;
            }
            printMiddleMenuLine(frameLength, menuOptionRow);
        }

        printHorizontalLine(frameLength);

        // Print the [0 - Exit] or [0 - Log Out] - at the bottom of the list below another separator
        printMiddleMenuLine(frameLength, menuOptions.get(0));

        printHorizontalLine(frameLength);
    }

    /**
     * |      0 - Exit                                   |
     * |      1 - Login                                  |
     *
     * @param frameLength - longest row data + min spaces on each side + 2
     * @param rowData     - the actual data that needs to be printed "0 - Exit"
     */
    private static void printMiddleMenuLine(int frameLength, String rowData) {
        String coloredFrameAndSpacesBeginningOfRow = ConsolePrinter.getGreenMsg("|" + " ".repeat(MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU));
        System.out.print(coloredFrameAndSpacesBeginningOfRow + rowData);
        System.out.println(ConsolePrinter.getGreenMsg(" ".repeat(getNumberOfRemainingSpacesToTheEndOfTheFrame(frameLength, rowData)) + "|"));
    }

    private static int getNumberOfRemainingSpacesToTheEndOfTheFrame(int frameLength, String rowData) {
        return (frameLength - (getStringLengthWithoutANSI(rowData) + MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU + 2));
    }

    private static int getTheNumberOfSymbolsInTheLongestString(String str, List<String> listOfStrings) {
        // Create a new list that combines the current list and the string and get the length of the longest one.
        List<String> menuQuestionAndOptions = new ArrayList<>(listOfStrings);
        menuQuestionAndOptions.add(str);
        return getLengthOfTheLongestStringInList(menuQuestionAndOptions);
    }

    private static void printHorizontalLine(int frameLength) {
        String coloredFrameHorizontal = ConsolePrinter.getGreenMsg(MENU_LINE_SYMBOL.repeat(frameLength));
        System.out.println(coloredFrameHorizontal);
    }

    private static String addTrailingSpacesBeginningOfString(String str, int desiredLength) {
        return " ".repeat(desiredLength - str.length()) + str;
    }

    private static int getNumberOfSymbolsInSetOfIntegers(Set<Integer> set) {
        int longest = 0;
        for (int num : set) {
            String word = String.valueOf(num);
            if (word.length() > longest) {
                longest = word.length();
            }
        }
        return longest;
    }

    private static int getLengthOfTheLongestStringInList(List<String> list) {
        int longest = 0;
        for (String row : list) {
            int currentLength = getStringLengthWithoutANSI(row);
            if (currentLength > longest) {
                longest = currentLength;
            }
        }
        return longest;
    }

    private static int getStringLengthWithoutANSI(String str) {
        return str.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }
}
