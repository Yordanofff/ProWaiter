package FrontEnd;

import BackEnd.Users.User;

import java.util.Scanner;

public class UserInput {
    static Scanner scanner = new Scanner(System.in);

    private static String getUserInput(String question) {
        System.out.println(question);
        return scanner.nextLine();
    }

    public static String getUsername() {
        String username;
        while (true) {
            String question = "Please enter username: ";
            String userInput = getUserInput(question);

            if (Validators.isValidUsername(userInput, User.MINIMUM_USERNAME_LENGTH)) {
                username = userInput;
                if (Validators.isCapitalLetterInString(username)) {
                    ConsolePrinter.printWarning("The username [" + username + "] will be converted to lowercase letters only");
                }
                break;
            } else {
                System.out.println("Try again.");
            }
        }
        return username.toLowerCase();
    }

    public static String getPassword(String username) {
        String password;
        while (true) {
            String question = "Please enter a password for " + username + ": ";
            String userInput = getUserInput(question);

            if (Validators.isPasswordValid(userInput, User.MINIMUM_USERNAME_PASSWORD_LENGTH)) {
                password = userInput;
                break;
            } else {
                System.out.println("Try again.");
            }
        }
        return password;
    }

    public static String getFirstName() {
        return getName("first");
    }

    public static String getLastName() {
        return getName("last");
    }

    private static String getName(String firstOrLastName) {
        String nameToReturn;
        while (true) {
            String question = "Please enter your " + firstOrLastName + " name: ";
            String userInput = getUserInput(question);

            if (Validators.isValidName(userInput)) {
                nameToReturn = userInput;
                if (!Validators.isNameWrittenCorrectly(nameToReturn)) {
                    String fixedCapitalizationName = UserInput.capitalize(nameToReturn);
                    ConsolePrinter.printWarning("Incorrect capitalization of " + firstOrLastName +
                            " name [" + nameToReturn + "]. Will be renamed to [" + fixedCapitalizationName + "].");
                    nameToReturn = fixedCapitalizationName;
                }
                break;
            } else {
                System.out.println("Try again.");
            }
        }
        return nameToReturn;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
