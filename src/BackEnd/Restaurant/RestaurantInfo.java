package BackEnd.Restaurant;

import BackEnd.DB.DBOperations;
import FrontEnd.ConsolePrinter;
import FrontEnd.UserInput;

public class RestaurantInfo {
    private String restaurantName;
    private int numberOfTables;

    public RestaurantInfo(String name, int numberTables) {
        restaurantName = name;
        numberOfTables = numberTables;
    }

    public RestaurantInfo() {
    }

    @Override
    public String toString() {
        return "RestaurantInfo{" +
                "restaurantName='" + restaurantName + '\'' +
                ", numberOfTables=" + numberOfTables +
                '}';
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String newRestaurantName) {
        restaurantName = newRestaurantName;
    }

    public int getNumberOfTables() {
        return numberOfTables;
    }

    public void setNumberOfTables(int numberTables) {
        numberOfTables = numberTables;
    }

    static void saveRestaurantInfoInDB(RestaurantInfo restaurantInfo) {
        DBOperations.setRestaurantInfo(restaurantInfo);
        ConsolePrinter.printInfo("Restaurant Info written to DB: " + restaurantInfo);
    }

    public static RestaurantInfo getRestaurantInfoFromDB() {
        return DBOperations.getRestaurantInfoFromDB();
    }

    public RestaurantInfo getNewRestaurantInfoFromUser() {
        String currentName = getRestaurantName();
        int currentNumTables = getNumberOfTables();

        String newRestaurantName = getNewRestaurantNameFromUser();
        int newNumberOfTables = getNewNumberOfTablesFromUser();

        if (currentName == null) {
            ConsolePrinter.printInfo("Restaurant name set to [" + newRestaurantName + "]");
        } else if (currentName.equals(newRestaurantName)) {
            ConsolePrinter.printInfo("Restaurant name won't change.");
        } else {
            ConsolePrinter.printInfo("Restaurant name changed from [" + currentName + "] to [" + newRestaurantName + "]");
        }

        if (currentNumTables == 0) {
            ConsolePrinter.printInfo("Restaurant table count set to [" + newNumberOfTables + "]");
        } else if (currentNumTables == newNumberOfTables) {
            ConsolePrinter.printInfo("Restaurant table count won't change.");
        } else {
            ConsolePrinter.printInfo("Restaurant table count changed from [" + currentNumTables + "] to [" + newRestaurantName + "]");
        }
        return new RestaurantInfo(newRestaurantName, newNumberOfTables);
    }

    private String getNewRestaurantNameFromUser() {
        String currentName = getRestaurantName();
        if (currentName != null) {
            ConsolePrinter.printInfo("Current restaurant name [" + currentName + "]");
            if (UserInput.getConfirmation("Do you want to change it?")) {
                return UserInput.getUserInput("Enter restaurant name: ");
            } else {
                return currentName;
            }
        }
        return UserInput.getUserInput("Enter restaurant name: ");
    }

    private int getNewNumberOfTablesFromUser() {
        int currentNumTables = getNumberOfTables();
        if (currentNumTables != 0) {
            ConsolePrinter.printInfo("Current tables count: [" + currentNumTables + "]");
            // todo - just add extra tables? What if less tables?
            ConsolePrinter.printWarning("Changing the number of tables will result in closing all current Orders.");
            if (UserInput.getConfirmation("Do you want to change it?")) {
                return UserInput.getIntInput("Enter number of tables in the restaurant");
            } else {
                return currentNumTables;
            }
        }
        return UserInput.getIntInput("Enter number of tables in the restaurant");
    }

}
