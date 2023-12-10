package FrontEnd;

import BackEnd.Restaurant.Dishes.*;
import BackEnd.Restaurant.Menu.RestaurantMenu;
import BackEnd.Users.User;

import java.sql.SQLException;
import java.util.List;

import static FrontEnd.MenuBuilder.*;
import static FrontEnd.UserInput.getUserInputFrom0toNumber;
import static FrontEnd.UserInput.pressAnyKeyToContinue;

public class RestaurantMenuBuilder {

    public static void restaurantMenuItemsMenu(User user) {
        String[] menuOptions = new String[]{"Print Restaurant Menu", "Add new item", "Delete item"};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Restaurant Menu Options";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, RestaurantMenuBuilder::RestaurantMenuItemsMenuOptions, user); // use this if user data is needed in WaiterMenuAction
    }

    public static void RestaurantMenuItemsMenuOptions(int option, User user) {
        switch (option) {
            case 1 -> printRestaurantMenu();
            case 2 -> addNewItemToRestaurantMenu(user);
            case 3 -> deleteItemFromRestaurantMenu();
        }
    }

    public static void printRestaurantMenu() {
        List<Dish> allFood = RestaurantMenu.getAllFood();
        List<Dish> allDrink = RestaurantMenu.getAllDrink();
        List<Dish> allDesert = RestaurantMenu.getAllDesert();

        String columnNames = "Name, Price";

        int[] maxColumnLengths = getBiggest(
                RestaurantMenu.joinDishToString(allFood, false),
                RestaurantMenu.joinDishToString(allDrink, false),
                RestaurantMenu.joinDishToString(allDesert, false),
                columnNames
        );

        printColumnNames(maxColumnLengths, columnNames);

        printCategoryMenu(allFood, "Food", maxColumnLengths);
        printCategoryMenu(allDrink, "Drinks", maxColumnLengths);
        printCategoryMenu(allDesert, "Deserts", maxColumnLengths);

        pressAnyKeyToContinue();
    }

    public static void printCategoryMenu(List<Dish> categoryDishes, String categoryName, int[] maxColumnLengths) {
        List<String> categoryCommaSeparated = RestaurantMenu.joinDishToString(categoryDishes, false);
        printMenuOptionsInFrameTableRestaurantMenu(categoryCommaSeparated, categoryName, "", "", maxColumnLengths);
    }

    public static void addNewItemToRestaurantMenu(User user) {
        String[] dishTypeNames = Dish.getDishTypeNames();

        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Select the type of Dish you would like to add: ";
        String optionZeroText = "Back";
        String optionZeroMsg = "Going back..";
        buildMenu(dishTypeNames, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> addNewItemToRestaurantMenuAction(option), user);  // lambda function to ignore the user.
    }

    public static void addNewItemToRestaurantMenuAction(int option) {
        DishType selectedDishType = getDishTypeFromOption(option);
        addNewItemToRestaurantMenuDish(selectedDishType);
    }

    private static DishType getDishTypeFromOption(int option) {
        return switch (option) {
            case 1 -> DishType.FOOD;
            case 2 -> DishType.DRINK;
            case 3 -> DishType.DESSERT;
            default -> throw new IllegalArgumentException("Invalid DishType option: " + option);
        };
    }

    public static void addNewItemToRestaurantMenuDish(DishType dishType) {
        String dishName = UserInput.getUserInput("Please enter [" + dishType + "] name:");
        double dishPrice = UserInput.getDoubleInput("Please enter price for [" + dishName + "]: ");
        Dish dish = new Dish(dishName, dishPrice, dishType);
        RestaurantMenu.addDish(dish);
    }

    public static void deleteItemFromRestaurantMenu() {
        printAllRestaurantDishesWithNumbers("Go Back");

        List<String> allDishesCommaSeparated = getMergedListOfNestedStringLists(getAllThreeDishesForMenu());

        ConsolePrinter.printQuestion("Enter the index of the item that you wish to delete: ");

        int selection = getUserInputFrom0toNumber(allDishesCommaSeparated.size());

        if (selection == 0) {
            System.out.println("Going back..");
            return;
        }

        String dishName = getFirstElementFromIndex(selection, allDishesCommaSeparated);

        boolean confirmed = UserInput.getConfirmation("Are you sure you want to delete [" + dishName + "]");
        if (confirmed) {
            try {
                RestaurantMenu.removeDishName(dishName);
            } catch (SQLException e){
                ConsolePrinter.printError("Dish [" + dishName +"] cannot be removed because it's used in an [ordered Dish]");
            }
            // msg will be printed from the RestaurantMenu
        } else {
            System.out.println("Cancelling..");
        }
    }

    public static void printAllRestaurantDishesWithNumbers(String zeroOptionText) {
        List<List<String>> allThreeDishes = getAllThreeDishesForMenu();
        List<String> food = allThreeDishes.get(0);
        List<String> drink = allThreeDishes.get(1);
        List<String> dessert = allThreeDishes.get(2);

        String columnNames = "Index, Name, Price";

        int[] maxColumnLengths = getMaxColumnLengthsAcrossLists(allThreeDishes, columnNames);

        printMenuOptionsInFrameTableRestaurantMenu(food, "Food", columnNames, "", maxColumnLengths);
        printMenuOptionsInFrameTableRestaurantMenu(drink, "Drinks", "", "", maxColumnLengths);
        printMenuOptionsInFrameTableRestaurantMenu(dessert, "Deserts", "", zeroOptionText, maxColumnLengths);
    }

    public static List<List<String>> getAllThreeDishesForMenu() {
        List<String> allFoodCommaSeparated = getDishToStringWithNumbers(RestaurantMenu.getAllFood(), 1);
        List<String> allDrinkCommaSeparated = getDishToStringWithNumbers(RestaurantMenu.getAllDrink(), allFoodCommaSeparated.size() + 1);
        List<String> allDessertCommaSeparated = getDishToStringWithNumbers(RestaurantMenu.getAllDesert(), allFoodCommaSeparated.size() + allDrinkCommaSeparated.size() + 1);

        return MenuBuilder.combineLists(allFoodCommaSeparated, allDrinkCommaSeparated, allDessertCommaSeparated);
    }

    public static List<String> getDishToStringWithNumbers(List<Dish> dishes, int startNumber){
        return RestaurantMenu.joinDishToString(dishes, false, true, startNumber);
    }
}
