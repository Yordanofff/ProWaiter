package BackEnd.Restaurant.Menu;

import BackEnd.DB.DBOperations;
import BackEnd.Restaurant.Dishes.*;
import FrontEnd.ConsolePrinter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static FrontEnd.Validators.formatDecimalNumber;

public class RestaurantMenu {
    //  Static fields, as the data will be the same for all.
    private static List<Dish> dishes = new ArrayList<>();

    @Override
    public String toString() {
        return "RestaurantMenu{" +
                "dishes=" + dishes +
                '}';
    }

    public static void addDish(Dish dish) {
        if (!isDishAlreadyInMenu(dish)) {
            dishes.add(dish);
            DBOperations.addDishToRestaurantMenuItems(dish);
        }
    }

    public static void setDishesFromDB() {
        // To be run when the app starts + if the user wants to update (if other users have updated it)
        // Reason is to lower down DB calls
        List<Dish> allDishes = DBOperations.getAllDishesFromRestaurantMenuItems();
        setDishes(allDishes);
    }

    public static void removeDish(Dish dish) throws SQLException {
        if (isDishAlreadyInMenu(dish, false)) {
            // Try to remove it from DB first, so if it fails - it will still be shown in the menu.
            boolean result = DBOperations.removeDishFromRestaurantMenuItems(dish);
            dishes.remove(dish);
            if (result) {
                ConsolePrinter.printInfo("Dish [" + dish.getName() + "] removed successfully.");
            }
        } else {
            ConsolePrinter.printError("Dish [" + dish.getName() + "] not found in the Restaurant Menu!");
        }
    }

    public static void removeDishName(String dishName) throws SQLException {
        Dish dishToRemove = getDishFromDishName(dishName);
        if (dishToRemove == null) {
            return;
        }
        removeDish(dishToRemove);
    }

    public static Dish getDishFromDishName(String dishName) {
        for (Dish dish : getDishes()) {
            if (dish.getName().equalsIgnoreCase(dishName)) {
                return dish;
            }
        }
        ConsolePrinter.printError("Dish [" + dishName + "] not found in the Restaurant Menu!");
        return null;
    }

    public static List<Dish> getDishes() {
        // Populate the dishes list when the app starts.
        if (dishes.isEmpty()) {
            setDishesFromDB();
        }
        return dishes;
    }

    public static List<Dish> getAllFood() {
        List<Dish> allFood = new ArrayList<>();
        for (Dish dish : getDishes()) {
            if (dish.getDishType() == DishType.FOOD) {
                allFood.add(dish);
            }
        }
        return allFood;
    }

    public static List<Dish> getAllDrink() {
        List<Dish> allDrink = new ArrayList<>();
        for (Dish dish : getDishes()) {
            if (dish.getDishType() == DishType.DRINK) {
                allDrink.add(dish);
            }
        }
        return allDrink;
    }

    public static List<Dish> getAllDesert() {
        List<Dish> allDesert = new ArrayList<>();
        for (Dish dish : getDishes()) {
            if (dish.getDishType() == DishType.DESSERT) {
                allDesert.add(dish);
            }
        }
        return allDesert;
    }

    private static void setDishes(List<Dish> newDishes) {
        dishes = newDishes;
    }

    private static boolean isDishAlreadyInMenu(Dish dish, boolean printError) {
        for (Dish dishInMenu : getDishes()) {
            if (dishInMenu.getName().equalsIgnoreCase(dish.getName())) {
                if (printError) {
                    ConsolePrinter.printError("Dish [" + dish.getName() + "] already in the menu.");
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isDishAlreadyInMenu(Dish dish) {
        return isDishAlreadyInMenu(dish, true);
    }

    public static List<String> joinDishToString(List<Dish> dishes, boolean addDishType, boolean addNumbers, int startNumber) {
        List<String> result = new ArrayList<>();
        String dataToAdd = "";

        for (Dish dish : dishes) {
            dataToAdd = "";
            if (addNumbers) {
                dataToAdd += startNumber + ", ";
                startNumber++;
            }
            dataToAdd += dish.getName() + ", " + formatDecimalNumber(dish.getPrice());
            if (addDishType) {
                dataToAdd += ", " + dish.getDishType();

            }
            result.add(dataToAdd);
        }
        return result;
    }

    public static List<String> joinDishToString(List<Dish> dishes, boolean addDishType) {
        return joinDishToString(dishes, addDishType, false, 0);
    }

}
