package BackEnd.DB;

import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Dishes.OrderedDish;
import BackEnd.Restaurant.Order;
import BackEnd.Restaurant.OrderStatus;
import BackEnd.Restaurant.RestaurantInfo;
import BackEnd.Restaurant.Table;
import BackEnd.Users.User;
import FrontEnd.ConsolePrinter;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;
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
        dao.createTablesTableIfNotExist();

        dao.createOrderStatusesTableIfNotExist();
        if (!areAllOrderStatusesInDB()){
            ConsolePrinter.printWarning("OrderStatuses not found in the DB. Adding them now.");
            dao.populateOrderStatusesTable();
        }

        dao.createOrdersTableIfNotExist();
//        dao.createDishesTableIfNotExist();  // not needed? Will delete later.
        dao.createOrderDishesTableIfNotExist();
    }
    public boolean areAllOrderStatusesInDB(){
        List<String> orderStatuses = getOrderStatusesTable();
        boolean areAllOrderStatusesInDB = true;

        for (OrderStatus status: OrderStatus.values()) {
            boolean isFound = false;
            for (String orderStatus: orderStatuses) {
                if (status.toString().equals(orderStatus)) {
                    isFound = true;
                }
            }
            if (!isFound) {
                areAllOrderStatusesInDB = false;
                break;
            }
        }
        return areAllOrderStatusesInDB;
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

    public boolean removeDishFromRestaurantMenuItems(Dish dish) throws SQLException {
        return dao.removeDishFromRestaurantMenuItems(dish);
    }

    public void setRestaurantInfo(RestaurantInfo restaurantInfo){
        dao.setRestaurantInfo(restaurantInfo);
    }

    public RestaurantInfo getRestaurantInfoFromDB() {
        return dao.getRestaurantInfoFromDB();
    }

    public List<Table> getAllTablesFromDB() {
        return dao.getAllTablesFromDB();
    }

    public boolean writeTablesToDB(List<Table> tables){
        return dao.writeTablesToDB(tables);
    }

    public boolean updateOccupyTable(Table table) {
        return dao.updateOccupyTable(table);
    }

    public void addOrderToOrdersTable(Order order){
        dao.addOrderToOrdersTable(order);
    }

    public void updateOrderDishesToDB(Order order) {
        dao.updateOrderDishesToDB(order);
    }

    public List<String> getOrderStatusesTable() {
        return dao.getOrderStatusesTable();
    }

    public List<OrderedDish> getOrdersDishesForTableNumber(int tableNumber) {
        return dao.getOrdersDishesForTableNumber(tableNumber);
    }

    public long getOrderIDOfOccupiedTable(int tableNumber) {
        return dao.getOrderIDOfOccupiedTable(tableNumber);
    }

    public Order getCurrentOrderForTable(int tableNumber) {
        return dao.getCurrentOrderForTable(tableNumber);
    }

    public void deleteOrderByID(Order order) {
        dao.deleteOrderByID(order);
    }

    public boolean updateOrderStatus(Order order) {
        return dao.updateOrderStatus(order);
    }

    public List<Order> getAllOrdersFromDB() {
        return dao.getAllOrdersFromDB();
    }

    public List<Order> getAllOrdersFromDBWithStatus(OrderStatus orderStatus) {
        return dao.getAllOrdersFromDBWithStatus(orderStatus);
    }

    public List<OrderedDish> getOrdersDishesForID(long id) {
        return dao.getOrdersDishesForID(id);
    }
}