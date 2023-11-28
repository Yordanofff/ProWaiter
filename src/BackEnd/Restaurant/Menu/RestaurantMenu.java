package BackEnd.Restaurant.Menu;

import BackEnd.DB.DBOperations;
import BackEnd.Restaurant.Dishes.*;
import FrontEnd.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;

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

    public void removeDish(Dish dish) {
        // todo: removing dishes will be by selecting the dish number in the menu (should have all data about the dish)
        // todo: mark as not-available? - re-add later on/tomorrow?
        dishes.remove(dish);
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

    public static List<Food> getFood() {
        List<Food> allFood = new ArrayList<>();
        for (Dish dish : getDishes()) {
            if (dish.getDishType() == DishType.FOOD) {
                allFood.add((Food) dish);
            }
        }
        return allFood;
    }

    public static List<Drink> getDrink() {
        List<Drink> allDrink = new ArrayList<>();
        for (Dish dish : getDishes()) {
            if (dish.getDishType() == DishType.DRINK) {
                allDrink.add((Drink) dish);
            }
        }
        return allDrink;
    }

    public static List<Dessert> getDesert() {
        List<Dessert> allDesert = new ArrayList<>();
        for (Dish dish : getDishes()) {
            if (dish.getDishType() == DishType.DESSERT) {
                allDesert.add((Dessert) dish);
            }
        }
        return allDesert;
    }

    private static void setDishes(List<Dish> newDishes) {
        dishes = newDishes;
    }

    private static boolean isDishAlreadyInMenu(Dish dish) {
        for (Dish dishInMenu : getDishes()) {
            if (dishInMenu.getName().equalsIgnoreCase(dish.getName())) {
                ConsolePrinter.printError("Dish [" + dish.getName() + "] already in the menu.");
                return true;
            }
        }
        return false;
    }
}
