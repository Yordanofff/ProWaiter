package BackEnd.DB;

import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Users.User;

import java.util.List;

public class DBOperations {
    // Use this class to do DB Operations without initializing new PostgreSQL() everywhere.
    public static PostgreSQL database = new PostgreSQL();

    public static void printDBTables() {
        database.printDBTables();
    }

    public static List<User> getUsers(int limit) {
        return database.getUsers(limit);
    }

    public static void addUser(User user) {
        database.addUser(user);
    }

    public static void deleteUserByUsername(String userName) {
        database.deleteUserByUsername(userName);
    }

    public static void addDishToRestaurantMenuItems(Dish dish) {
        database.addDishToRestaurantMenuItems(dish);
    }

    public static List<Dish> getAllDishesFromRestaurantMenuItems() {
        return database.getAllDishesFromRestaurantMenuItems();
    }

    public static boolean removeDishFromRestaurantMenuItems(Dish dish) {
        return database.removeDishFromRestaurantMenuItems(dish);
    }
}
