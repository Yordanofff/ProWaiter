package BackEnd.DB;

import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Dishes.OrderedDish;
import BackEnd.Restaurant.Order;
import BackEnd.Restaurant.RestaurantInfo;
import BackEnd.Restaurant.Table;
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

    public static void setRestaurantInfo(RestaurantInfo restaurantInfo){
        database.setRestaurantInfo(restaurantInfo);
    }

    public static RestaurantInfo getRestaurantInfoFromDB() {
        return database.getRestaurantInfoFromDB();
    }

    public static List<Table> getAllTablesFromDB() {
        return database.getAllTablesFromDB();
    }

    public static boolean writeTablesToDB(List<Table> tables){
        return database.writeTablesToDB(tables);
    }

    public static boolean updateOccupyTable(Table table) {
        return database.updateOccupyTable(table);
    }

    public static void addOrderToOrdersTable(Order order){
        database.addOrderToOrdersTable(order);
    }

    public static void updateOrderDishesToDB(Order order) {
        database.updateOrderDishesToDB(order);
    }

    public static List<String> getOrderStatusesTable() {
        return database.getOrderStatusesTable();
    }

    public static List<OrderedDish> getOrdersDishesForTableNumber(int tableNumber) {
        return database.getOrdersDishesForTableNumber(tableNumber);
    }

    public static long getOrderIDOfOccupiedTable(int tableNumber) {
        return database.getOrderIDOfOccupiedTable(tableNumber);
    }

    public static Order getCurrentOrderForTable(int tableNumber) {
        return database.getCurrentOrderForTable(tableNumber);
    }

    public static void deleteOrderByID(Order order) {
        database.deleteOrderByID(order);
    }
}
