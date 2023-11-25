package FrontEnd;

import BackEnd.Users.User;
import BackEnd.Users.UserManager;
import BackEnd.Users.UserType;

import java.util.*;

public class MenuBuilder {
    private static final String sep = ",";  // separator for the strings when printing menus
    static final int MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU = 5;
    static final String MENU_SEPARATOR = " - ";  // With spaces if required.

    static final String topLeftCorner = "┌";
    static final String bottomLeftCorner = "└";
    static final String midLeft = "├";
    static final String topRightCorner = "┐";
    static final String bottomRightCorner = "┘";
    static final String midRight = "┤";
    static final String topBottom = "─";
    static final String SideWall = "│";
    static Scanner scanner = new Scanner(System.in);

    public static void buildLoginMenu() {
        String[] menuOptions = new String[]{"Login"};
        String topMenuLabel = "Please Login with your credentials:";
        String optionZeroText = "Exit";
        String optionZeroMsg = "GoodBye!";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, MenuBuilder::LoginMenuAction);
    }

    public static void LoginMenuAction(int option) {
        // Call methods to run
        switch (option) {
            case 1 -> UserLoginMenuAction();
        }
    }

    public static void UserLoginMenuAction() {
        User user = UserManager.getTheLoginUserIfUsernameAndPasswordAreCorrect();
        if (user != null) {
            UserType userType = user.getUserType();
            System.out.println("Opening [" + userType + "] panel...");
            if (userType == UserType.ADMIN) {
                buildAdminMenu(user);
            } else if (userType == UserType.KITCHEN) {
                buildKitchenMenu(user);
            } else if (userType == UserType.WAITER) {
                buildWaiterMenu(user);
            }
        }
    }

    public static void buildAdminMenu(User user) {
        String[] menuOptions = new String[]{"User Management", "Menu Management", "Order management"};
        String topMenuLabel = "[" + user.getUserType() + "] Hello, " + user.getFullName() + "!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, MenuBuilder::AdminMenuAction);
    }

    public static void AdminMenuAction(int option) {
        // Call methods to run
        switch (option) {
            case 1 -> buildUserManagementMenu();
            case 2 -> System.out.println("Admin Action 2");
            case 3 -> System.out.println("Admin Action 3");
        }
    }

    public static void buildUserManagementMenu() {
        String[] menuOptions = new String[]{"View all users", "Add user", "Edit user", "Delete user"};
        String topMenuLabel = "Hello admin!";  // todo - user.type + name
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back to main admin menu..."; // todo - user.type..
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, MenuBuilder::UserManagementMenuAction);
    }

    public static void UserManagementMenuAction(int option) {
        // Call methods to run
        switch (option) {
            // todo
            case 1 -> UserManager.printAllUsers();
            case 2 -> System.out.println(".. call method - add user");
            case 3 -> System.out.println(".. call method - edit user");
            case 4 -> System.out.println(".. call method - Delete user");
        }
    }

    public static void buildKitchenMenu(User user) {
        // todo - ready should only show orders with status "Cooking"
        String[] menuOptions = new String[]{"Show orders", "Set status: cooking", "Set status: ready"};
        String topMenuLabel = "[" + user.getUserType() + "] Hello, " + user.getFullName() + "!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, MenuBuilder::KitchenMenuAction);
    }

    public static void KitchenMenuAction(int option) {
        // Call methods to run
        switch (option) {
            // todo
            case 1 -> System.out.println("Showing orders..");
            case 2 -> System.out.println("Showing orders.. + user input - select order - status cooking");
            case 3 -> System.out.println("Showing orders.. + user input - select order - status ready");
        }
    }

    public static void buildWaiterMenu(User user) {
        // todo - ready should only show orders with status "Cooking"
        String[] menuOptions = new String[]{"Show orders", "Show ready orders", "Add order", "Add to order", "Remove from order", "Set status: served"};
        String topMenuLabel = "[" + user.getUserType() + "] Hello, " + user.getFullName() + "!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, MenuBuilder::WaiterMenuAction);
    }

    public static void WaiterMenuAction(int option) {
        // Call methods to run
        switch (option) {
            // todo
            case 1 -> System.out.println(".. waiter option 1");
            case 2 -> System.out.println(".. waiter option 2");
        }
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
            menuAction.execute(selectedOption);

            // pause
            System.out.print("Press any key to continue..: ");
            scanner.nextLine();

            System.out.println();
        }
    }

    private static int printMenuAndGetUsersChoice(String[] menuOptions, String topMenuQuestion, String optionZeroText) {
        // Creating HashMap with numbers and options. + Adding Exit/Logout/Go Back
        HashMap<Integer, String> menuOptionsWithNumbers = generateHashMapMenuOptionsWithNumbers(menuOptions, optionZeroText);

        // Fixing spaces before the numbers, adding separator and storing all in a list.
        List<String> dataToPrint = getMenuOptionsWithSameLengthOfMaxDigitLength(menuOptionsWithNumbers);

        // Printing the options in a frame
        printMenuOptionsInFrameNew(topMenuQuestion, dataToPrint);

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
     * |      0 - Exit                                   |
     * |      1 - Login                                  |
     *
     * @param frameLength - longest row data + min spaces on each side + 2
     * @param rowData     - the actual data that needs to be printed "0 - Exit"
     */
    private static void printMiddleMenuLine(int frameLength, String rowData, String sideSymbol) {
        String coloredFrameAndSpacesBeginningOfRow = ConsolePrinter.getGreenMsg(sideSymbol + " ".repeat(MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU));
        System.out.print(coloredFrameAndSpacesBeginningOfRow + rowData);
        System.out.println(ConsolePrinter.getGreenMsg(" ".repeat(getNumberOfRemainingSpacesToTheEndOfTheFrame(frameLength, rowData)) + sideSymbol));
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

    public static void printMenuOptionsInFrameNew(String menuTopQuestion, List<String> menuOptions) {

        int longestRowWithData = getTheNumberOfSymbolsInTheLongestString(menuTopQuestion, menuOptions);

        // Add the minimum spaces on both sides of the string + 2 symbols for the frame (left + right side)
        int frameLength = longestRowWithData + (MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU * 2) + 2;

        System.out.println(getTopLineOfMenu(frameLength));
        printMiddleMenuLine(frameLength, menuTopQuestion, SideWall);
        System.out.println(getMidLine(frameLength));

        // The first element will be [0 - Exit] or [0 - Log Out] etc.. Don't print it in the top part.
        boolean isZeroElement = true;
        for (String menuOptionRow : menuOptions) {
            if (isZeroElement) {
                isZeroElement = false;
                continue;
            }
            printMiddleMenuLine(frameLength, menuOptionRow, SideWall);
        }

        System.out.println(getMidLine(frameLength));

        // Print the [0 - Exit] or [0 - Log Out] - at the bottom of the list below another separator
        printMiddleMenuLine(frameLength, menuOptions.get(0), SideWall);

        System.out.println(getBottomLine(frameLength));
    }


    private static void printElementsTable(List<String> myList, int[] maxColumnLengths) {
        int numSpacesAroundEachColumnWord = 2;
        int maxNumberOfSymbolsAllRows = getMaxNumberOfSymbolsAllRows(maxColumnLengths);
        int numberOfColumns = maxColumnLengths.length; // todo
        int numAddedSpaces = numberOfColumns * 2 * numSpacesAroundEachColumnWord;
        // top frame len = maxColumnLength + (numberOfColumns +1) (1 symbol each column + sides) + numAddedSpaces
        int frameLength = maxNumberOfSymbolsAllRows + numAddedSpaces + numberOfColumns + 1;
        System.out.println("_".repeat(frameLength));
        for (String row : myList) {
            String[] elements = getRowElementsTrimmed(row);

            for (int i = 0; i < elements.length; i++) {
                String currentElement = elements[i];
                int maxLengthCurrentPosition = maxColumnLengths[i];

                System.out.print("|" + " ".repeat(numSpacesAroundEachColumnWord) + currentElement +
                        " ".repeat(maxLengthCurrentPosition - currentElement.length() + numSpacesAroundEachColumnWord));
                if (i == elements.length - 1) {
                    System.out.println("|");
                }
            }
        }
        System.out.println("_".repeat(frameLength));
    }


    private static String getTopLineOfMenu(int length, String label) {
        int numDashesEachSide = length - label.length() - 2 - 2; // 2 for the spaces, 2 for the corners
        if (label.isEmpty()) {
            return getGreenLine(length, topLeftCorner, topRightCorner);
        }
        String dashesSide = topBottom.repeat(numDashesEachSide / 2);
        String spacesAfterLabel = " ";
        if (numDashesEachSide % 2 == 0) {
            spacesAfterLabel = "  ";
        }
        return ConsolePrinter.getGreenMsg(topLeftCorner + dashesSide + " ")
                + label + ConsolePrinter.getGreenMsg(spacesAfterLabel + dashesSide + topRightCorner);
    }

    private static String getTopLineOfMenu(int length) {
        return getTopLineOfMenu(length, "");
    }

    private static String getGreenLine(int length, String mostLeftSymbol, String mostRightSymbol) {
        return ConsolePrinter.getGreenMsg(mostLeftSymbol + topBottom.repeat(length - 2) + mostRightSymbol);
    }

    private static String getMidLine(int length) {
        return getGreenLine(length, midLeft, midRight);
    }

    private static String getBottomLine(int length) {
        return getGreenLine(length, bottomLeftCorner, bottomRightCorner);
    }


    private static int getMaxNumberOfSymbolsAllRows(int[] maxColumnLengths) {
        int n = 0;
        for (int el : maxColumnLengths) {
            n += el;
        }
        return n;
    }


    private static int[] getMaxColumnLengths(List<String> myList) {
        int numberOfColumns = getNumberOfElements(myList);
        int[] elementsLength = new int[numberOfColumns];
        for (String row : myList) {

            String[] elements = getRowElementsTrimmed(row);

            for (int i = 0; i < elements.length; i++) {
                int currentElementLength = elements[i].length();
                if (elementsLength[i] < currentElementLength) {
                    elementsLength[i] = currentElementLength;
                }
            }
        }
        return elementsLength;
    }

    private static String[] getRowElementsTrimmed(String row) {
        // Create elements - trimmed - single row
        String[] elements = row.split(sep);
        for (int i = 0; i < elements.length; i++) {
            elements[i] = elements[i].trim();
        }
        return elements;
    }

    private static int getNumberOfElements(List<String> myList) {

        String[] elements = myList.get(0).split(sep);
        return elements.length;

    }
}


// todo - will be deleted on the next commit.
//static final String MENU_LINE_SYMBOL = "-";

//    private static void printMiddleMenuLine(int frameLength, String rowData) {
//        printMiddleMenuLine(frameLength, rowData, "|");
//    }
//
//    private static void printHorizontalLine(int frameLength) {
//        String coloredFrameHorizontal = ConsolePrinter.getGreenMsg(MENU_LINE_SYMBOL.repeat(frameLength));
//        System.out.println(coloredFrameHorizontal);
//    }

//    /**
//     * This method will be used to print the menu options. Adds a frame that will be surrounded
//     * by at least MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU spaces (for the longest word) and more for the rest.
//     * ===============================================
//     * |     menuTopQuestion                         |
//     * ===============================================
//     * |     1 - menuOptions 1                       |
//     * |     2 - menuOptions 2                       |
//     * |     3 - menuOptions 3 ...                   |
//     * ===============================================
//     *
//     * @param menuTopQuestion - Top menu question
//     * @param menuOptions     - List of options
//     */
//    public static void printMenuOptionsInFrame(String menuTopQuestion, List<String> menuOptions) {
//
//        int longestRowWithData = getTheNumberOfSymbolsInTheLongestString(menuTopQuestion, menuOptions);
//
//        // Add the minimum spaces on both sides of the string + 2 symbols for the frame (left + right side)
//        int frameLength = longestRowWithData + (MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU * 2) + 2;
//
//        printHorizontalLine(frameLength);
//        printMiddleMenuLine(frameLength, menuTopQuestion);
//        printHorizontalLine(frameLength);
//
//        // The first element will be [0 - Exit] or [0 - Log Out] etc.. Don't print it in the top part.
//        boolean isZeroElement = true;
//        for (String menuOptionRow : menuOptions) {
//            if (isZeroElement) {
//                isZeroElement = false;
//                continue;
//            }
//            printMiddleMenuLine(frameLength, menuOptionRow);
//        }
//
//        printHorizontalLine(frameLength);
//
//        // Print the [0 - Exit] or [0 - Log Out] - at the bottom of the list below another separator
//        printMiddleMenuLine(frameLength, menuOptions.get(0));
//
//        printHorizontalLine(frameLength);
//    }