package FrontEnd;

import BackEnd.Users.User;
import BackEnd.Users.UserManager;
import BackEnd.Users.UserType;

import java.util.*;

import static FrontEnd.MenuBuilderFrameDrawers.*;

public class MenuBuilder {
    public static final String sep = ",";  // separator for the strings when printing menus
    static final int MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU = 5;
    static final String MENU_SEPARATOR = " - ";  // With spaces if required.
    static final String SideWall = "│";
    static Scanner scanner = new Scanner(System.in);
    static int numSpacesAroundEachColumnWord = 2;

    public static void LoginMenu() {
        // todo - if enough time - add another option to change the color of the menu.
        String[] menuOptions = new String[]{"Login", "About"};
        String frameLabel = "";  // No frame label on the Login Menu page.
        String topMenuLabel = "Please Login with your credentials:";
        String optionZeroText = "Exit";
        String optionZeroMsg = "GoodBye!";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> LoginMenuAction(option), null);
    }

    public static void LoginMenuAction(int option) {
        // Call methods to run
        switch (option) {
            case 1 -> UserLoginMenuAction();
            case 2 -> aboutPage();
        }
    }

    private static void aboutPage() {
        ConsolePrinter.printQuestion("\nHello, we are [Ivaylo Yordanov] and [Ivaylo Staykov] and we love coding.\n\nWe really hope that you'll like our project.\n");
    }

    public static void UserLoginMenuAction() {
        User user = UserManager.getTheLoginUserIfUsernameAndPasswordAreCorrect();
        if (user != null) {
            UserType userType = user.getUserType();
            System.out.println("Opening [" + userType + "] panel...");
            if (userType == UserType.ADMIN) {
                AdminMenu(user);
            } else if (userType == UserType.COOK) {
                KitchenMenu(user);
            } else if (userType == UserType.WAITER) {
                WaiterMenu(user);
            }
        }
    }

    public static void AdminMenu(User user) {
        String[] menuOptions = new String[]{"User Management", "Menu Management", "Order management"};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Hello, " + user.getFullName() + "!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, MenuBuilder::AdminMenuAction, user);
    }

    public static void AdminMenuAction(int option, User user) {
        // Call methods to run
        switch (option) {
            case 1 -> UserManagementMenuBuilder.UserManagementMenu(user);
            case 2 -> RestaurantMenuBuilder.restaurantMenuItemsMenu(user);
            case 3 -> OrdersOperationsMenuBuilder.ordersMenu(user);
        }
    }

    public static void KitchenMenu(User user) {
        OrdersOperationsMenuBuilder.kitchenOrdersMenu(user);
    }

    public static void KitchenMenuAction(int option, User user) {
        // todo - use user somewhere?
        switch (option) {
            case 1 -> System.out.println("Showing orders..");
            case 2 -> System.out.println("Showing orders.. + user input - select order - status cooking");
            case 3 -> System.out.println("Showing orders.. + user input - select order - status ready");
        }
    }

    public static void WaiterMenu(User user) {  // orders menu?
        String[] menuOptions = new String[]{"Restaurant Menu Options", "Orders Menu Options"};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Hello, " + user.getFullName() + "!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, MenuBuilder::WaiterMenuAction); // use this if user data is needed in WaiterMenuAction
//        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> WaiterMenuAction(option), user);  // lambda function to ignore the user.
    }

    public static void WaiterMenuAction(int option, User user) {
        switch (option) {
            case 1 -> RestaurantMenuBuilder.restaurantMenuItemsMenu(user);
            case 2 -> OrdersOperationsMenuBuilder.ordersMenu(user);
        }
    }

    public static String getElementPositionFromIndex(int index, List<String> allDishesCommaSeparated, int positionToGet) {
        for (String row : allDishesCommaSeparated) {
            if (Integer.parseInt(row.split(sep)[0]) == index) {
                return row.split(sep)[positionToGet].strip();
            }
        }
        return null;
    }

    public static String getFirstElementFromIndex(int index, List<String> allDishesCommaSeparated) {
        return getElementPositionFromIndex(index, allDishesCommaSeparated, 1);
    }

    public static void buildMenu(String[] menuOptions, String topMenuLabel, String optionZeroText, String optionZeroMsg, String frameLabel, MenuAction menuAction, User user) {
        while (true) {
            int selectedOption = printMenuAndGetUsersChoice(menuOptions, topMenuLabel, optionZeroText, frameLabel);

            // Exit if 0
            if (selectedOption == 0) {
                System.out.println(optionZeroMsg);
                break;
            }

            // Executing the selected option
            menuAction.execute(selectedOption, user);

            // pause
//            System.out.print("Press any key to continue..: ");
//            scanner.nextLine();

            System.out.println();
        }
    }

    public static void buildMenu(String[] menuOptions, String topMenuLabel, String optionZeroText, String optionZeroMsg, String frameLabel, MenuAction menuAction) {
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, menuAction, null);
    }

    public static int buildMenuOrder(int[] menuOptions, String topMenuLabel, String optionZeroText, String optionZeroMsg, String frameLabel, String freeOrOccupiedTable) {
        boolean validOption = false;
        int selectedOption;
        while (true) {
            selectedOption = printMenuAndGetUsersChoiceOrder(menuOptions, topMenuLabel, optionZeroText, frameLabel, freeOrOccupiedTable);

            // Exit if 0
            if (selectedOption == 0) {
                System.out.println(optionZeroMsg);
                break;
            }

            if (isNumberInArray(menuOptions, selectedOption)) {
                validOption = true;
                break;
            }

            // pause
            System.out.print("Press any key to continue..: ");
            scanner.nextLine();

            System.out.println();
        }

        if (validOption) {
            // Executing the selected option
            return selectedOption;
        }
        return 0;
    }

    private static int printMenuAndGetUsersChoiceOrder(int[] tables, String topMenuQuestion, String optionZeroText, String frameLabel, String freeOrOccupiedTable) {
        // Creating HashMap with numbers and table text - 1, Free table; 2, Free table ...
        HashMap<Integer, String> menuOptionsWithNumbers = generateHashMapMenuOptionsWithNumbersOrder(tables, optionZeroText, freeOrOccupiedTable);

        // Fixing spaces before the numbers, adding separator and storing all in a list.
        List<String> dataToPrint = getMenuOptionsWithSameLengthOfMaxDigitLength(menuOptionsWithNumbers);

        // Printing the options in a frame
        printMenuOptionsInFrame(topMenuQuestion, dataToPrint, frameLabel);

        // Returning the user selection
        return getUserInputFrom0toNumberOrder(tables);
    }

    static int getUserInputFrom0toNumberOrder(int[] possibleTables) {
        int choice;

        while (true) {
            String ans = scanner.nextLine().strip();

            try {
                choice = Integer.parseInt(ans);

                if (choice == 0 || isNumberInArray(possibleTables, choice)) {
                    break;
                } else {
                    ConsolePrinter.printError("Please enter a number from [" + intArrayToString(possibleTables) + "] or [0]");
                }
            } catch (NumberFormatException e) {
                ConsolePrinter.printError("Invalid input [" + ans + "]! " +
                        "Please enter an integer from [" + intArrayToString(possibleTables) + "] or [0]");
            }
        }

        return choice;
    }

    private static boolean isNumberInArray(int[] array, int targetNumber) {
        for (int number : array) {
            if (number == targetNumber) {
                return true;
            }
        }
        return false;
    }

    private static String intArrayToString(int[] array) {
        String[] stringArray = new String[array.length];

        for (int i = 0; i < array.length; i++) {
            stringArray[i] = String.valueOf(array[i]);
        }

        return String.join(", ", stringArray);
    }

    private static HashMap<Integer, String> generateHashMapMenuOptionsWithNumbersOrder(int[] tables, String optionZeroText, String freeOrOccupiedTable) {
        // Will add Exit as position 0.

        // Using LinkedHashMap to keep the order as they were added.
        HashMap<Integer, String> menuOptionsWithNumbers = new LinkedHashMap<>();
        menuOptionsWithNumbers.put(0, optionZeroText);  // Exit/LogOut/Go Back

        for (int table : tables) {
            menuOptionsWithNumbers.put(table, freeOrOccupiedTable);
        }

        return menuOptionsWithNumbers;
    }

    static int printMenuAndGetUsersChoice(String[] menuOptions, String topMenuQuestion, String optionZeroText, String frameLabel) {
        // Creating HashMap with numbers and options. + Adding Exit/Logout/Go Back
        HashMap<Integer, String> menuOptionsWithNumbers = generateHashMapMenuOptionsWithNumbers(menuOptions, optionZeroText);

        // Fixing spaces before the numbers, adding separator and storing all in a list.
        List<String> dataToPrint = getMenuOptionsWithSameLengthOfMaxDigitLength(menuOptionsWithNumbers);

        // Printing the options in a frame
        printMenuOptionsInFrame(topMenuQuestion, dataToPrint, frameLabel);

        // Returning the user selection
        return getUserInputFrom0toNumber(menuOptions.length);
    }

    static int getUserInputFrom0toNumber(int numOptions) {
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

    static int[] getUserInputMenuNumberAndQuantity(int numOptions) {
        int[] choiceAndQuantity = new int[2];
        int menuItemChoiceInt;
        int quantityInt;

        while (true) {
            String ans = scanner.nextLine().strip();
            String[] answers = ans.split(" ");

            if (answers.length > 2) {
                ConsolePrinter.printError("Maximum of [2] integers allowed in the format: [DISH INDEX] <space> [QUANTITY]");
                continue;
            } else if (answers.length == 0) {
                ConsolePrinter.printError("Empty Input. Try again.");
                continue;
            }

            String menuItemChoice;
            String quantity;

            if (answers.length == 1) {
                quantityInt = 1;

                try {
                    menuItemChoiceInt = Integer.parseInt(answers[0]);

                    if (menuItemChoiceInt >= 0 && menuItemChoiceInt <= numOptions) {
                        break;
                    } else {
                        ConsolePrinter.printError("Please enter a number between [0 - " + numOptions + "]");
                    }
                } catch (NumberFormatException e) {
                    ConsolePrinter.printError("Invalid input [" + ans + "]! " +
                            "Please enter an integer in the range [0 - " + numOptions + "]");
                }
            } else {
                // answers.length == 2
                menuItemChoice = answers[0];
                quantity = answers[1];

                if (!(isInteger(menuItemChoice) && isInteger(quantity))) {
                    ConsolePrinter.printError("Invalid input! Only integers allowed!");
                    continue;
                }

                quantityInt = Integer.parseInt(quantity);
                if (quantityInt <= 0) {
                    ConsolePrinter.printError("Quantity cannot be a [negative number] or [0]");
                    continue;
                }

                menuItemChoiceInt = Integer.parseInt(answers[0]);
                if (menuItemChoiceInt >= 0 && menuItemChoiceInt <= numOptions) {
                    break;
                } else {
                    ConsolePrinter.printError("Please enter a number between [0 - " + numOptions + "]");
                }
            }
        }

        choiceAndQuantity[0] = menuItemChoiceInt;
        choiceAndQuantity[1] = quantityInt;
        return choiceAndQuantity;
    }

    private static boolean isInteger(String integerToTest) {
        try {
            Integer.parseInt(integerToTest);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
    private static void printMiddleMenuLine(int frameLength, String rowData, String sideSymbol, int numberOfSymbolsFromLeftWall) {
        String coloredFrameAndSpacesBeginningOfRow = ConsolePrinter.getGreenMsg(sideSymbol + " ".repeat(numberOfSymbolsFromLeftWall));
        System.out.print(coloredFrameAndSpacesBeginningOfRow + rowData);
        if (numberOfSymbolsFromLeftWall == MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU) {
            System.out.println(ConsolePrinter.getGreenMsg(" ".repeat(getNumberOfRemainingSpacesToTheEndOfTheFrame(frameLength, rowData)) + sideSymbol));
        } else {
            int diff = MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU - numberOfSymbolsFromLeftWall;
            frameLength = frameLength + diff;  // add more spaces when printing the frame so that the right wall is in the correct place.
            System.out.println(ConsolePrinter.getGreenMsg(" ".repeat(getNumberOfRemainingSpacesToTheEndOfTheFrame(frameLength, rowData)) + sideSymbol));
        }
    }

    private static void printMiddleMenuLine(int frameLength, String rowData, String sideSymbol) {
        printMiddleMenuLine(frameLength, rowData, sideSymbol, MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU);
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

    /**
     * ┌────────── [ADMIN] ───────────┐
     * │     Hello, Ivo Yordanov!     │
     * ├──────────────────────────────┤
     * │     1 - User Management      │
     * │     2 - Menu Management      │
     * │     3 - Order management     │
     * ├──────────────────────────────┤
     * │     0 - Log out              │
     * └──────────────────────────────┘
     *
     * @param menuTopQuestion "Hello, Ivo Yordanov!"
     * @param menuOptions     - List of Strings with all Options: ["0 - Log out", "1 - User Management", ..]
     * @param frameLabel      - [ADMIN]
     */
    public static void printMenuOptionsInFrame(String menuTopQuestion, List<String> menuOptions, String frameLabel) {

        int longestRowWithData = getTheNumberOfSymbolsInTheLongestString(menuTopQuestion, menuOptions);

        // Add the minimum spaces on both sides of the string + 2 symbols for the frame (left + right side)
        int frameLength = longestRowWithData + (MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU * 2) + 2;

        System.out.println(getTopLineOfMenu(frameLength, frameLabel));
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

    public static void printMenuOptionsInFrameTable(List<String> rowsWithCommaSeparatedColumns, String frameLabel, String columnNames, String zeroOptionText) {

        int[] maxColumnLengths = getBiggestColumnNames(rowsWithCommaSeparatedColumns, columnNames);
        int numberOfColumns = getMaxNumberOfColumns(maxColumnLengths, columnNames);

        // This sums up the longest word in each column
        int maxNumberOfSymbolsAllRows = getMaxNumberOfSymbolsAllRows(maxColumnLengths);

        int numAddedSpaces = numberOfColumns * 2 * numSpacesAroundEachColumnWord;

        // top frame len = maxColumnLength + (numberOfColumns +1) (1 symbol each column + sides) + numAddedSpaces
        int frameLength = maxNumberOfSymbolsAllRows + numAddedSpaces + numberOfColumns + 1;

        System.out.println(getTopLineOfMenu(frameLength, frameLabel));
        System.out.println(getTopLineTableEndingUpDown(frameLength, maxColumnLengths));
        columnNames = addExtraSeparatorsToLength(columnNames, numberOfColumns);
        printMiddleMenuLineTable(columnNames, maxColumnLengths, numSpacesAroundEachColumnWord);
        System.out.println(getMidLineTable(frameLength, maxColumnLengths));

        for (String row : rowsWithCommaSeparatedColumns) {
            row = addExtraSeparatorsToLength(row, numberOfColumns);
            printMiddleMenuLineTable(row, maxColumnLengths, numSpacesAroundEachColumnWord);
        }
        if (zeroOptionText.isEmpty()) {
            System.out.println(getBottomLineTable(frameLength, maxColumnLengths));
        } else {
            System.out.println(getBottomLineTableContinuingDownCorners(frameLength, maxColumnLengths));
            zeroOptionText = "0 - " + zeroOptionText;
            printMiddleMenuLine(frameLength, zeroOptionText, SideWall, numSpacesAroundEachColumnWord);
            System.out.println(getBottomLine(frameLength));
        }
    }

    public static void printMenuOptionsInFrameTable(List<String> rowsWithCommaSeparatedColumns, String frameLabel, String columnNames) {
        printMenuOptionsInFrameTable(rowsWithCommaSeparatedColumns, frameLabel, columnNames, "");
    }

    /**
     * ┌──────────────────┬─────────┐
     * │  Name            │  Price  │
     * └──────────────────┴─────────┘
     * ┌─────────── Food ───────────┐
     * ├──────────────────┬─────────┤
     * │  Pizza           │  12.99  │
     * │  Burger          │  8.49   │
     * │  Salad           │  6.99   │
     * └──────────────────┴─────────┘
     * ┌────────── Drinks ──────────┐
     * ├──────────────────┬─────────┤
     * │  Iced Tea        │  1.99   │
     * │  Soda            │  2.49   │
     * │  Smoothie        │  4.95   │
     * └──────────────────┴─────────┘
     * ┌───────── Deserts ──────────┐
     * ├──────────────────┬─────────┤
     * │  Ice Cream       │  3.5    │
     * │  Cheesecake      │  6.75   │
     * │  Chocolate Cake  │  5.99   │
     * ├──────────────────┴─────────┤
     * │  0 - Go Back               │
     * └────────────────────────────┘
     *
     * @param rowsWithCommaSeparatedColumns Pizza, 12.99 ; Burger, 8.49 ...etc
     * @param frameLabel                    Food
     * @param columnNames                   Name, Price (When empty - not printing)
     * @param zeroOptionText                0, Go Back
     * @param maxColumnLengths              [14, 5] - the longest word on the left/right column or more columns
     */
    public static void printMenuOptionsInFrameTableRestaurantMenu(List<String> rowsWithCommaSeparatedColumns, String frameLabel, String columnNames, String zeroOptionText, int[] maxColumnLengths) {

        int numberOfColumns = getMaxNumberOfColumns(maxColumnLengths, columnNames);

        // This sums up the longest word in each column
        int maxNumberOfSymbolsAllRows = getMaxNumberOfSymbolsAllRows(maxColumnLengths);

        int numAddedSpaces = numberOfColumns * 2 * numSpacesAroundEachColumnWord;

        // top frame len = maxColumnLength + (numberOfColumns +1) (1 symbol each column + sides) + numAddedSpaces
        int frameLength = maxNumberOfSymbolsAllRows + numAddedSpaces + numberOfColumns + 1;

        if (!columnNames.isEmpty()) {
            printColumnNames(frameLength, maxColumnLengths, columnNames);
        }
        System.out.println(getTopLineOfMenu(frameLength, frameLabel));
        System.out.println(getTopLineTableEndingUpDown(frameLength, maxColumnLengths));

        for (String row : rowsWithCommaSeparatedColumns) {
            row = addExtraSeparatorsToLength(row, numberOfColumns);
            printMiddleMenuLineTable(row, maxColumnLengths, numSpacesAroundEachColumnWord);
        }
        if (zeroOptionText.isEmpty()) {
            System.out.println(getBottomLineTable(frameLength, maxColumnLengths));
        } else {
            printZeroOptionText(frameLength, maxColumnLengths, zeroOptionText);
        }
    }

    public static void printColumnNames(int frameLength, int[] maxColumnLengths, String columnNames){
        int numberOfColumns = getMaxNumberOfColumns(maxColumnLengths, columnNames);
        System.out.println(getTopLineTable(frameLength, maxColumnLengths));
        columnNames = addExtraSeparatorsToLength(columnNames, numberOfColumns);
        printMiddleMenuLineTable(columnNames, maxColumnLengths, numSpacesAroundEachColumnWord);
        System.out.println(getBottomLineTable(frameLength, maxColumnLengths));
    }

    public static void printZeroOptionText(int frameLength, int[] maxColumnLengths, String zeroOptionText) {
        System.out.println(getBottomLineTableContinuingDownCorners(frameLength, maxColumnLengths));
        zeroOptionText = "0 - " + zeroOptionText;
        printMiddleMenuLine(frameLength, zeroOptionText, SideWall, numSpacesAroundEachColumnWord);
        System.out.println(getBottomLine(frameLength));
    }

    public static int getFrameLength(int[] maxColumnLengths, String columnNames) {
        int numberOfColumns = getMaxNumberOfColumns(maxColumnLengths, columnNames);
        int maxNumberOfSymbolsAllRows = getMaxNumberOfSymbolsAllRows(maxColumnLengths);
        int numAddedSpaces = numberOfColumns * 2 * numSpacesAroundEachColumnWord;
        return maxNumberOfSymbolsAllRows + numAddedSpaces + numberOfColumns + 1;
    }

    private static String addExtraSeparatorsToLength(String columnNames, int numberOfColumns) {
        // Add extra , to the columns so that it prints walls on right side if columns are more than column names.
        if (numberOfColumns > columnNames.split(sep).length) {
            int diff = numberOfColumns - columnNames.split(sep).length;
            columnNames += ", ".repeat(diff);
        }
        return columnNames;
    }

    static int getMaxNumberOfColumns(int[] maxColumnLengths, String columnNames) {
        int numberOfColumns = maxColumnLengths.length;
        if (getNumberOfColumns(columnNames) > numberOfColumns) {
            numberOfColumns = getNumberOfColumns(columnNames);
        }
        return numberOfColumns;
    }

    static int[] getBiggestColumnNames(List<String> rowsWithCommaSeparatedColumns, String columnNames) {
        int[] maxColumnLengths = getMaxColumnLengths(rowsWithCommaSeparatedColumns);
        // Get the length of the elements in the columnNames
        int[] maxColumnLengthDescription = getMaxColumnLength(columnNames);
        maxColumnLengths = getBiggest(maxColumnLengths, maxColumnLengthDescription);
        return maxColumnLengths;
    }

    private static int[] getBiggest(int[] array1, int[] array2) {
        int maxLength = Math.max(array1.length, array2.length);
        int[] result = new int[maxLength];

        for (int i = 0; i < maxLength; i++) {
            int value1 = (i < array1.length) ? array1[i] : 0; // Use 0 if array1 is shorter
            int value2 = (i < array2.length) ? array2[i] : 0; // Use 0 if array2 is shorter

            result[i] = Math.max(value1, value2);
        }

        return result;
    }

    private static int[] getBiggest(int[] array1, int[] array2, int[] array3) {
        int[] maxOfFirstTwo = getBiggest(array1, array2);
        return getBiggest(maxOfFirstTwo, array3);
    }

    static int[] getBiggest(List<String> l1, List<String> l2, List<String> l3, String columnNames) {
        int[] maxColumnLengthsFood = getBiggestColumnNames(l1, columnNames);
        int[] maxColumnLengthsDrink = getBiggestColumnNames(l2, columnNames);
        int[] maxColumnLengthsDesert = getBiggestColumnNames(l3, columnNames);

        int[] maxColumnLengths = getBiggest(maxColumnLengthsFood, maxColumnLengthsDrink, maxColumnLengthsDesert);
        return maxColumnLengths;
    }

    private static int[] getLastXElements(int[] sourceArray, int x) {
        // Ensure x is not greater than the length of the source array
        x = Math.min(x, sourceArray.length);

        int[] resultArray = new int[x];

        // Copy the last x elements from the source array to the result array
        for (int i = 0; i < x; i++) {
            resultArray[i] = sourceArray[sourceArray.length - x + i];
        }

        return resultArray;
    }

    static int getMaxNumberOfSymbolsAllRows(int[] maxColumnLengths) {
        int n = 0;
        for (int el : maxColumnLengths) {
            n += el;
        }
        return n;
    }

    private static int[] getMaxColumnLengths(List<String> myList) {
        int numberOfColumns = getNumberOfColumns(myList);
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

    private static int[] getMaxColumnLength(String row) {
        int numberOfColumns = getNumberOfColumns(row);
        int[] elementsLength = new int[numberOfColumns];

        String[] elements = getRowElementsTrimmed(row);

        for (int i = 0; i < elements.length; i++) {
            int currentElementLength = elements[i].length();
            if (elementsLength[i] < currentElementLength) {
                elementsLength[i] = currentElementLength;
            }
        }
        return elementsLength;
    }

    static String[] getRowElementsTrimmed(String row) {
        // Create elements - trimmed - single row
        String[] elements = row.split(sep);
        for (int i = 0; i < elements.length; i++) {
            elements[i] = elements[i].trim();
        }
        return elements;
    }

    private static int getNumberOfColumns(List<String> myList) {
        if (myList.isEmpty()) {
            return 0;
        }

        String[] firstRow = myList.get(0).split(sep);
        return firstRow.length;
    }

    private static int getNumberOfColumns(String myString) {
        String[] row = myString.split(sep);
        return row.length;
    }

    static String getStringForInteger(HashMap<Integer, String> map, int key, int columnToReturn) {
        String data = map.get(key);
        String[] columns = data.split(sep);
        return columns[columnToReturn];
    }

    static List<String> createListWithCommaSeparatedValues(Map<Integer, String> map) {
        List<String> resultList = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            String commaSeparatedValue = entry.getKey() + sep + entry.getValue();
            resultList.add(commaSeparatedValue);
        }

        return resultList;
    }

    static HashMap<Integer, String> mapNumbersToItems(String[] items, int startingNumber) {
        // Using LinkedHashMap to keep the order as they were added.
        HashMap<Integer, String> itemsWithNumbers = new LinkedHashMap<>();

        for (int i = startingNumber; i < items.length + startingNumber; i++) {
            itemsWithNumbers.put(i, items[i - 1]);
        }

        return itemsWithNumbers;
    }

    static List<String> getMergedLists(List<String> l1, List<String> l2) {
        List<String> mergedList = new ArrayList<>();
        mergedList.addAll(l1);
        mergedList.addAll(l2);
        return mergedList;
    }

    public static List<String> getMergedListOfNestedStringLists(List<List<String>> toMerge) {
        List<String> merged = new ArrayList<>();
        for (List<String> singleList : toMerge) {
            merged = getMergedLists(merged, singleList);
        }
        return merged;
    }

    static void pressAnyKeyToContinue() {
        ConsolePrinter.printQuestion("Press [Enter] to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    @SafeVarargs
    static List<List<String>> combineLists(List<String>... lists) {
        List<List<String>> combinedList = new ArrayList<>();

        for (List<String> list : lists) {
            // Make a defensive copy to ensure the original lists are not modified externally
            List<String> newList = new ArrayList<>(list);
            combinedList.add(newList);
        }

        return combinedList;
    }
}