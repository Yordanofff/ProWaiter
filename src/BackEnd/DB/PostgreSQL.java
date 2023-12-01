package BackEnd.DB;

import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.RestaurantInfo;
import BackEnd.Users.User;
import FrontEnd.ConsolePrinter;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.List;

public class PostgreSQL {
    private DataAccessObject dao;

    public PostgreSQL() {
        init();
    }

    private void init() {
        // Configure the database connection.
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setApplicationName("ProWaiter");

        // todo - config file/sys env
//        ds.setUrl(System.getenv("JDBC_DATABASE_URL"));
        String user = "ivo";
        String pass = "secret";
        String url = "jdbc:postgresql://test1rz-11759.8nj.cockroachlabs.cloud:26257/Waiter?sslmode=require&password=" + pass + "&user=" + user;
        ds.setUrl(url);

        // Create DAO.
        this.dao = new DataAccessObject(ds);

        // Create the accounts table if it doesn't exist
        dao.createUserTableIfNotExist();
        dao.createRestaurantMenuTableIfNotExist();
        dao.createRestaurantInfoIfNotExist();
        // todo - create Orders/Tables
    }

    public List<User> getUsers(int limit) {
        return dao.getUsers(limit);
    }

    public void addUser(User user) {
        dao.addUser(user);
    }

    public void deleteUserByUsername(String userName) {
        dao.deleteUserByUsername(userName);
    }

    public void printDBTables() {
        List<String> tables = dao.getDBTables();
        for (String table : tables) {
            System.out.println(table);
        }
    }

    public void addDishToRestaurantMenuItems(Dish dish) {
        dao.addDishToRestaurantMenuItems(dish);
        ConsolePrinter.printInfo("[" + dish.getDishType() + "] [" + dish.getName() + "] was added to the Restaurant Menu!");
    }

    public List<Dish> getAllDishesFromRestaurantMenuItems() {
        return dao.getAllDishesFromRestaurantMenuItems();
    }

    public boolean removeDishFromRestaurantMenuItems(Dish dish) {
        return dao.removeDishFromRestaurantMenuItems(dish);
    }

    public void setRestaurantInfo(RestaurantInfo restaurantInfo){
        dao.setRestaurantInfo(restaurantInfo);
    }

    public RestaurantInfo getRestaurantInfoFromDB() {
        return dao.getRestaurantInfoFromDB();
    }
}