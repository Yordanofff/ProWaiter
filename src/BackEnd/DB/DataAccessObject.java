package BackEnd.DB;

import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Dishes.DishType;
import BackEnd.Restaurant.Dishes.OrderedDish;
import BackEnd.Restaurant.Order;
import BackEnd.Restaurant.OrderStatus;
import BackEnd.Restaurant.RestaurantInfo;
import BackEnd.Restaurant.Table;
import BackEnd.Users.User;
import BackEnd.Users.UserByUserType;
import BackEnd.Users.UserType;
import FrontEnd.ConsolePrinter;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

// File from the CocroachDB java example https://github.com/cockroachlabs/example-app-java-jdbc/blob/master/app/src/main/java/com/cockroachlabs/BasicExample.java

public class DataAccessObject {
    private static final int MAX_RETRY_COUNT = 3;
    private static final String RETRY_SQL_STATE = "40001";
    private static final boolean FORCE_RETRY = false;

    private final DataSource ds;

    private final Random rand = new Random();

    public DataAccessObject(DataSource ds) {
        this.ds = ds;
    }


    /**
     * Run SQL code in a way that automatically handles the
     * transaction retry logic so we don't have to duplicate it in
     * various places.
     *
     * @param sqlCode a String containing the SQL code you want to
     *                execute.  Can have placeholders, e.g., "INSERT INTO accounts
     *                (id, balance) VALUES (?, ?)".
     * @param args    String Varargs to fill in the SQL code's
     *                placeholders.
     * @return Integer Number of rows updated, or -1 if an error is thrown.
     */
    public Integer runSQL(String sqlCode, String... args) {

        // This block is only used to emit class and method names in
        // the program output.  It is not necessary in production
        // code.
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement elem = stacktrace[2];
        String callerClass = elem.getClassName();
        String callerMethod = elem.getMethodName();

        int rv = 0;

        try (Connection connection = ds.getConnection()) {

            // We're managing the commit lifecycle ourselves so we can
            // automatically issue transaction retries.
            connection.setAutoCommit(false);

            int retryCount = 0;

            while (retryCount <= MAX_RETRY_COUNT) {

                if (retryCount == MAX_RETRY_COUNT) {
                    String err = String.format("hit max of %s retries, aborting", MAX_RETRY_COUNT);
                    throw new RuntimeException(err);
                }

                // This block is only used to test the retry logic.
                // It is not necessary in production code.  See also
                // the method 'testRetryHandling()'.
                if (FORCE_RETRY) {
                    forceRetry(connection); // SELECT 1
                }

                try (PreparedStatement pstmt = connection.prepareStatement(sqlCode)) {

                    // Loop over the args and insert them into the
                    // prepared statement based on their types.  In
                    // this simple example we classify the argument
                    // types as "integers" and "everything else"
                    // (a.k.a. strings).
                    for (int i = 0; i < args.length; i++) {
                        int place = i + 1;
                        String arg = args[i];

                        try {
                            int val = Integer.parseInt(arg);
                            pstmt.setInt(place, val);
                        } catch (NumberFormatException e) {
                            pstmt.setString(place, arg);
                        }
                    }

                    if (pstmt.execute()) {
                        // We know that `pstmt.getResultSet()` will
                        // not return `null` if `pstmt.execute()` was
                        // true
                        ResultSet rs = pstmt.getResultSet();
                        ResultSetMetaData rsmeta = rs.getMetaData();
                        int colCount = rsmeta.getColumnCount();

                        // This printed output is for debugging and/or demonstration
                        // purposes only.  It would not be necessary in production code.
                        System.out.printf("\n%s.%s:\n    '%s'\n", callerClass, callerMethod, pstmt);  // todo

                        while (rs.next()) {
                            for (int i = 1; i <= colCount; i++) {
                                String name = rsmeta.getColumnName(i);
                                String type = rsmeta.getColumnTypeName(i);

                                // In this "bank account" example we know we are only handling
                                // integer values (technically 64-bit INT8s, the CockroachDB
                                // default).  This code could be made into a switch statement
                                // to handle the various SQL types needed by the application.
                                if ("int8".equals(type)) {
                                    int val = rs.getInt(name);

                                    // This printed output is for debugging and/or demonstration
                                    // purposes only.  It would not be necessary in production code.
                                    System.out.printf("    %-8s => %10s\n", name, val);  // todo
                                }
                            }
                        }
                    } else {
                        int updateCount = pstmt.getUpdateCount();
                        rv += updateCount;

                        // This printed output is for debugging and/or demonstration
                        // purposes only.  It would not be necessary in production code.
                        System.out.printf("\n%s.%s:\n    '%s'\n", callerClass, callerMethod, pstmt);  // todo
                    }

                    connection.commit();
                    break;

                } catch (SQLException e) {

                    if (RETRY_SQL_STATE.equals(e.getSQLState())) {
                        // Since this is a transaction retry error, we
                        // roll back the transaction and sleep a
                        // little before trying again.  Each time
                        // through the loop we sleep for a little
                        // longer than the last time
                        // (A.K.A. exponential backoff).
                        System.out.printf("retryable exception occurred:\n    sql state = [%s]\n    message = [%s]\n    retry counter = %s\n", e.getSQLState(), e.getMessage(), retryCount);
                        connection.rollback();
                        retryCount++;
                        int sleepMillis = (int) (Math.pow(2, retryCount) * 100) + rand.nextInt(100);
                        System.out.printf("Hit 40001 transaction retry error, sleeping %s milliseconds\n", sleepMillis);
                        try {
                            Thread.sleep(sleepMillis);
                        } catch (InterruptedException ignored) {
                            // Necessary to allow the Thread.sleep()
                            // above so the retry loop can continue.
                        }

                        rv = -1;
                    } else {
                        rv = -1;
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.printf("BasicExampleDAO.runSQL ERROR: { state => %s, cause => %s, message => %s }\n",
                    e.getSQLState(), e.getCause(), e.getMessage());
            rv = -1;
        }

        return rv;
    }

    /**
     * Helper method called by 'testRetryHandling'.  It simply issues
     * a "SELECT 1" inside the transaction to force a retry.  This is
     * necessary to take the connection's session out of the AutoRetry
     * state, since otherwise the other statements in the session will
     * be retried automatically, and the client (us) will not see a
     * retry error. Note that this information is taken from the
     * following test:
     * https://github.com/cockroachdb/cockroach/blob/master/pkg/sql/logictest/testdata/logic_test/manual_retry
     *
     * @param connection Connection
     */
    private void forceRetry(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT 1")) {
            statement.executeQuery();
        }
    }

    public void addUser(User user) {
        try (Connection connection = ds.getConnection()) {
            String sql = "INSERT INTO users (id, username, firstName, lastName, userType, password) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setObject(1, user.getId());
                statement.setString(2, user.getUsername());
                statement.setString(3, user.getFirstName());
                statement.setString(4, user.getLastName());
                statement.setString(5, user.getUserType().name());
                statement.setString(6, user.getPassword());

                // Execute the update
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }
    }

    public void deleteUserByUsername(String username) {
        try (Connection connection = ds.getConnection()) {
            String sql = "DELETE FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameter for the prepared statement
                statement.setString(1, username);

                // Execute the update
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }
    }

    public List<User> getUsers(int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users LIMIT ?";

        try (Connection connection = ds.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, limit);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        UserType userType = UserType.valueOf(resultSet.getString("userType"));

                        // Create user using UserByUserType
                        User user = UserByUserType.createUser(
                                userType,
                                resultSet.getString("firstName"),
                                resultSet.getString("lastName"),
                                resultSet.getString("username"),
                                resultSet.getString("password")
                        );

                        // Assign existing ID from the database
                        user.setId(UUID.fromString(resultSet.getString("id")));

                        users.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            // todo
            System.out.printf("BasicExampleDAO.bulkInsertRandomAccountData ERROR: { state => %s, cause => %s, message => %s }\n",
                    e.getSQLState(), e.getCause(), e.getMessage());
        }

        return users;
    }

    public void createUserTableIfNotExist() {
        runSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id UUID PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL," +
                "firstName VARCHAR(255)," +
                "lastName VARCHAR(255)," +
                "userType VARCHAR(50) NOT NULL," +
                "password VARCHAR(255) NOT NULL" +
                ")");
    }

    public void createRestaurantInfoIfNotExist() {
        runSQL("CREATE TABLE IF NOT EXISTS restaurantInfo (" +
                "restaurantName VARCHAR(255) NOT NULL," +
                "numberOfTables INT" +
                ")");
    }

//    public RestaurantInfo getRestaurantInfoFromDB() {
//
//        try (Connection connection = ds.getConnection();
//             Statement statement = connection.createStatement()) {
//
//            String sql = "SELECT restaurantName, numberOfTables FROM restaurantInfo";
//
//            try (ResultSet resultSet = statement.executeQuery(sql)) {
//                if (resultSet.next()) {
//                    String restaurantName = resultSet.getString("restaurantName");
//                    int numberOfTables = resultSet.getInt("numberOfTables");
//
//                    return new RestaurantInfo(restaurantName, numberOfTables);
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
//        }
//        return null;
//    }

    // Using PreparedStatement
    public RestaurantInfo getRestaurantInfoFromDB() {
        try (Connection connection = ds.getConnection()) {
            String sql = "SELECT restaurantName, numberOfTables FROM restaurantInfo";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    String restaurantName = resultSet.getString("restaurantName");
                    int numberOfTables = resultSet.getInt("numberOfTables");

                    return new RestaurantInfo(restaurantName, numberOfTables);
                }
            }

        } catch (SQLException e) {
            // Consider throwing a custom exception or logging the error for better error handling
            e.printStackTrace();
        }

        // Consider returning a default RestaurantInfo or throwing an exception based on your application logic
        return null;
    }


    public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
        try (Connection connection = ds.getConnection()) {
            String sql = "INSERT INTO restaurantInfo (restaurantName, numberOfTables) VALUES (?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, restaurantInfo.getRestaurantName());
                preparedStatement.setInt(2, restaurantInfo.getNumberOfTables());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }
    }

    public void createRestaurantMenuTableIfNotExist() {
        runSQL("CREATE TABLE IF NOT EXISTS restaurantMenuItems (" +
                "name VARCHAR(255) PRIMARY KEY," +
                "price REAL," +
                "dishType VARCHAR(50) NOT NULL)");
    }

    public void addDishToRestaurantMenuItems(Dish dish) {
        try (Connection connection = ds.getConnection()) {
            String sql = "INSERT INTO restaurantMenuItems (name, price, dishType) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, dish.getName());
                preparedStatement.setDouble(2, dish.getPrice());
                preparedStatement.setString(3, dish.getDishType().toString());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }
    }

    public boolean removeDishFromRestaurantMenuItems(Dish dish) {
        try (Connection connection = ds.getConnection()) {
            String sql = "DELETE FROM restaurantMenuItems WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameter for the prepared statement
                statement.setString(1, dish.getName());

                // Execute the update
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
            return false;
        }
    }

    public List<Dish> getAllDishesFromRestaurantMenuItems() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM restaurantMenuItems";
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
//                statement.setInt(1, limit);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        double price = resultSet.getDouble("price");
                        DishType dishType = DishType.valueOf(resultSet.getString("dishType"));
                        Dish dish = new Dish(name, price, dishType);
                        dishes.add(dish);
                    }
                }
            }
        } catch (SQLException e) {
            // todo
            System.out.printf("BasicExampleDAO.bulkInsertRandomAccountData ERROR: { state => %s, cause => %s, message => %s }\n",
                    e.getSQLState(), e.getCause(), e.getMessage());
        }
        return dishes;
    }

    public List<String> getDBTables() {
        List<String> tables = new ArrayList<>();
        String sql = "SHOW TABLES;";

        try (Connection connection = ds.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        tables.add(resultSet.getString("table_name"));
                    }

                }
            }
        } catch (SQLException e) {
            // todo
            System.out.printf("BasicExampleDAO.bulkInsertRandomAccountData ERROR: { state => %s, cause => %s, message => %s }\n",
                    e.getSQLState(), e.getCause(), e.getMessage());
        }

        return tables;
    }

    public void createTablesTableIfNotExist() {
        runSQL("CREATE TABLE IF NOT EXISTS Tables (" +
                "tableNumber INT PRIMARY KEY NOT NULL," +
                "isOccupied BOOLEAN NOT NULL)");
    }

    public List<Table> getAllTablesFromDB() {
        List<Table> tablesFromDB = new ArrayList<>();

        try (Connection connection = ds.getConnection()) {
            String sql = "SELECT tableNumber, isOccupied FROM Tables";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int tableNumber = resultSet.getInt("tableNumber");
                    boolean isOccupied = resultSet.getBoolean("isOccupied");

                    // Assuming you have a constructor in the Table class that takes tableNumber and isOccupied
                    Table table = new Table(tableNumber, isOccupied);

                    tablesFromDB.add(table);
                }
            }

        } catch (SQLException e) {
            // Consider throwing a custom exception or logging the error for better error handling
            e.printStackTrace();
        }

        return tablesFromDB;
    }

    public boolean writeTablesToDB(List<Table> tables) {
        if (tables == null || tables.isEmpty()) {
            ConsolePrinter.printError("No tables to write to the database.");
            return false;
        }

        try (Connection connection = ds.getConnection()) {
            String sql = "INSERT INTO Tables (tableNumber, isOccupied) VALUES (?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (Table table : tables) {
                    preparedStatement.setInt(1, table.getTableNumber());
                    preparedStatement.setBoolean(2, table.isOccupied());

                    // Add the current batch to the batch execution
                    preparedStatement.addBatch();
                }

                // Execute the batch insert statement
                int[] rowsAffected = preparedStatement.executeBatch();
                return Arrays.stream(rowsAffected).sum() == tables.size();
            }

        } catch (SQLException e) {
            // Handle exceptions based on your application's error handling strategy
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOccupyTable(Table table) {
        if (table == null) {
            throw new BadSqlDataException("No table to write to the database.");
        }

        if (table.isOccupied()) {
            // TODO: get table from DB
            // IF occupied already => throw exception
            // throw new TableOccupationException(); add throw in method.
        }

        try (Connection connection = ds.getConnection()) {
            String sql = "UPDATE Tables SET isOccupied = ? WHERE tableNumber = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setBoolean(1, table.isOccupied());
                preparedStatement.setInt(2, table.getTableNumber());
                // Execute the insert statement
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;

            }
        } catch (SQLException e) {
            // Handle exceptions based on your application's error handling strategy
            e.printStackTrace();
        }
        return false;
    }

    public void createOrdersTableIfNotExist() {
        runSQL("CREATE TABLE IF NOT EXISTS Orders (" +
                "id SERIAL PRIMARY KEY," +
                "tableNumber INT," +
                "isPaid BOOLEAN," +
                "statusName VARCHAR(50) NOT NULL," +
                "FOREIGN KEY (tableNumber) REFERENCES Tables(tableNumber)," +
                "FOREIGN KEY (statusName) REFERENCES OrderStatuses(statusName))"
        );
    }

    public void addOrderToOrdersTable(Order order) {
        try (Connection connection = ds.getConnection()) {
            String sql = "INSERT INTO Orders (tableNumber, isPaid, statusName) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, order.getTableNumber());
                preparedStatement.setBoolean(2, order.isPaid());
                preparedStatement.setString(3, order.getOrderStatus().toString());

                preparedStatement.executeUpdate();

                order.setOrderNumber(getOrderID(order));

//                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
//                    if (generatedKeys.next()) {
//                        order.setOrderNumber(generatedKeys.getLong(1));
//                    } else {
//                        throw new SQLException("Creating user failed, no ID obtained.");
//                    }
//                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }
    }

    public long getOrderID(Order order) throws SQLException {
        Connection connection = ds.getConnection();
        String sql = "SELECT id FROM Orders WHERE tableNumber = ? AND statusName != ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, order.getTableNumber());

        preparedStatement.setString(2, OrderStatus.PAID.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();

        return resultSet.getLong("id");
    }

    public List<Order> getAllOrdersFromDB() {
        List<Order> orders = new ArrayList<>();

        try (Connection connection = ds.getConnection()) {
            String sql = "SELECT id, tableNumber, isPaid, statusName FROM Orders";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int tableNumber = resultSet.getInt("tableNumber");
                    boolean isPaid = resultSet.getBoolean("isPaid");
                    String statusName = resultSet.getString("statusName");
                    int id = resultSet.getInt("id");

                    // Assuming you have a constructor in the Order class that takes these parameters
                    Order order = new Order(id, tableNumber, isPaid, OrderStatus.valueOf(statusName));
                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            // Handle exceptions based on your application's error handling strategy
            e.printStackTrace();
        }

        return orders;
    }

    public void createOrderStatusesTableIfNotExist() {
        runSQL("CREATE TABLE IF NOT EXISTS OrderStatuses (" +
                "statusName VARCHAR(50) PRIMARY KEY NOT NULL)"
        );

//        for (OrderStatus status : OrderStatus.values()) {
//            String statusName = status.toString();
//
//            try (Connection connection = ds.getConnection()) {
//                String sql = "INSERT INTO OrderStatuses (statusName) VALUES (?)";
//
//                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//
//                    preparedStatement.setString(1, statusName);
//
//                    preparedStatement.executeUpdate();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace(); // Handle the exception according to your application's error handling strategy
//            }
//        }
    }

    public void populateOrderStatusesTable() {
        for (OrderStatus status : OrderStatus.values()) {
            String statusName = status.toString();

            try (Connection connection = ds.getConnection()) {
                String sql = "INSERT INTO OrderStatuses (statusName) VALUES (?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                    preparedStatement.setString(1, statusName);

                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle the exception according to your application's error handling strategy
            }
        }
    }

    public List<String> getOrderStatusesTable() {
        List<String> orderStatuses = new ArrayList<>();

        String sql = "SELECT statusName FROM OrderStatuses";

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        orderStatuses.add(resultSet.getString("statusName"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.printf("BasicExampleDAO.bulkInsertRandomAccountData ERROR: { state => %s, cause => %s, message => %s }\n",
                    e.getSQLState(), e.getCause(), e.getMessage());
        }
        return orderStatuses;
    }

    public void createDishesTableIfNotExist() {
        runSQL("CREATE TABLE IF NOT EXISTS Dishes (" +
                "dishID INT PRIMARY KEY," +
                "orderNumber INT," +
                "dishName VARCHAR(255)," +
                "dishPrice DECIMAL(10,2)," +
                "FOREIGN KEY (orderNumber) REFERENCES Orders(id))");
    }

    public void createOrderDishesTableIfNotExist() {
        runSQL("CREATE TABLE IF NOT EXISTS OrdersDishes (" +
                "orderID INT," +
                "menuItemName VARCHAR(255)," +
                "quantity INT NOT NULL," +
                "FOREIGN KEY (orderID) REFERENCES Orders(id)," +
                "FOREIGN KEY (menuItemName) REFERENCES restaurantMenuItems(name))");
    }

    public void updateOrderDishesToDB(Order order) {
        // Clear the data in the order ID
        deleteOrderDishesFromDB(order);

        for (OrderedDish dish : order.getOrderedDishes()) {
            try (Connection connection = ds.getConnection()) {
                String sql = "INSERT INTO OrdersDishes (orderID, menuItemName, quantity) VALUES (?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                    preparedStatement.setLong(1, order.getOrderNumber());
                    preparedStatement.setString(2, dish.getDish().getName());
                    preparedStatement.setInt(3, dish.getQuantity());

                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle the exception according to your application's error handling strategy
            }
        }
    }

    public void deleteOrderDishesFromDB(Order order) {
        try (Connection connection = ds.getConnection()) {
            String sql = "DELETE FROM OrdersDishes WHERE orderID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, order.getOrderNumber());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }
    }


//    public int insertDish(Dish dish) {
//        int generatedDishID = -1; // Default value indicating an error
//
//        try (Connection connection = ds.getConnection()) {
//            String sql = "INSERT INTO Dishes (orderNumber, dishName, dishPrice) VALUES (?, ?, ?)";
//
//            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//                preparedStatement.setInt(1, dish.getOrderNumber());
//                preparedStatement.setString(2, dish.getName());
//                preparedStatement.setDouble(3, dish.getPrice());
//
//                int rowsAffected = preparedStatement.executeUpdate();
//
//                if (rowsAffected > 0) {
//                    // Retrieve the generated keys (including dishID)
//                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
//                        if (generatedKeys.next()) {
//                            generatedDishID = generatedKeys.getInt(1);
//                        }
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            // Handle exceptions based on your application's error handling strategy
//            e.printStackTrace();
//        }
//
//        return generatedDishID;
//    }

    public List<Dish> getDishesByOrderNumber(int orderNumber) {
        List<Dish> dishes = new ArrayList<>();

        try (Connection connection = ds.getConnection()) {
            String sql = "SELECT dishID, dishName, dishPrice FROM Dishes WHERE orderNumber = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, orderNumber);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int dishID = resultSet.getInt("dishID"); // todo ?
                        String dishName = resultSet.getString("dishName");
                        double dishPrice = resultSet.getDouble("dishPrice");

                        Dish dish = new Dish(dishName, dishPrice);
                        dishes.add(dish);
                    }
                }
            }

        } catch (SQLException e) {
            // Handle exceptions based on your application's error handling strategy
            e.printStackTrace();
        }

        return dishes;
    }
}


//    /**
//     Used to test the retry logic in 'runSQL'.  It is not necessary
//     in production code.
//     */
//    void testRetryHandling() {
//        if (DataAccessObject.FORCE_RETRY) {
//            runSQL("SELECT crdb_internal.force_retry('1s':::INTERVAL)");
//        }
//    }


//    /**
//     * Update accounts by passing in a Map of (ID, Balance) pairs.
//     *
//     * @param accounts (Map)
//     * @return The number of updated accounts (int)
//     */
//    public int updateAccounts(Map<String, String> accounts) {
//        int rows = 0;
//        for (Map.Entry<String, String> account : accounts.entrySet()) {
//
//            String k = account.getKey();
//            String v = account.getValue();
//
//            String[] args = {k, v};
//            rows += runSQL("INSERT INTO accounts (id, balance) VALUES (?, ?)", args);
//        }
//        return rows;
//    }


///**
//     * Get the account balance for one account.
//     *
//     * We skip using the retry logic in 'runSQL()' here for the
//     * following reasons:
//     *
//     * 1. Since this is a single read ("SELECT"), we don't expect any
//     *    transaction conflicts to handle
//     *
//     * 2. We need to return the balance as an integer
//     *
//     * @param id (UUID)
//     * @return balance (int)
//     */
//    public BigDecimal getAccountBalance(UUID id) {
//        BigDecimal balance = BigDecimal.valueOf(0);
//
//        try (Connection connection = ds.getConnection()) {
//
//            // Check the current balance.
//            ResultSet res = connection.createStatement()
//                    .executeQuery(String.format("SELECT balance FROM accounts WHERE id = '%s'", id.toString()));
//            if(!res.next()) {
//                System.out.printf("No users in the table with id %d", id);
//            } else {
//                balance = res.getBigDecimal("balance");
//            }
//        } catch (SQLException e) {
//            System.out.printf("BasicExampleDAO.getAccountBalance ERROR: { state => %s, cause => %s, message => %s }\n",
//                    e.getSQLState(), e.getCause(), e.getMessage());
//        }
//
//        return balance;
//    }


//    /**
//     * Insert randomized account data (ID, balance) using the JDBC
//     * fast path for bulk inserts.  The fastest way to get data into
//     * CockroachDB is the IMPORT statement.  However, if you must bulk
//     * ingest from the application using INSERT statements, the best
//     * option is the method shown here. It will require the following:
//     *
//     * 1. Add `rewriteBatchedInserts=true` to your JDBC connection
//     *    settings (see the connection info in 'BasicExample.main').
//     *
//     * 2. Inserting in batches of 128 rows, as used inside this method
//     *    (see BATCH_SIZE), since the PGJDBC driver's logic works best
//     *    with powers of two, such that a batch of size 128 can be 6x
//     *    faster than a batch of size 250.
//     * @return The number of new accounts inserted (int)
//     */
//    public int bulkInsertRandomAccountData() {
//
//        Random random = new Random();
//        int BATCH_SIZE = 128;
//        int totalNewAccounts = 0;
//
//        try (Connection connection = ds.getConnection()) {
//
//            // We're managing the commit lifecycle ourselves so we can
//            // control the size of our batch inserts.
//            connection.setAutoCommit(false);
//
//            // In this example we are adding 500 rows to the database,
//            // but it could be any number.  What's important is that
//            // the batch size is 128.
//            try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO accounts (id, balance) VALUES (?, ?)")) {
//                for (int i=0; i<=(500/BATCH_SIZE);i++) {
//                    for (int j=0; j<BATCH_SIZE; j++) {
//                        String id = UUID.randomUUID().toString();
//                        BigDecimal balance = BigDecimal.valueOf(random.nextInt(1000000000));
//                        pstmt.setString(1, id);
//                        pstmt.setBigDecimal(2, balance);
//                        pstmt.addBatch();
//                    }
//                    int[] count = pstmt.executeBatch();
//                    totalNewAccounts += count.length;
//                    System.out.printf("\nBasicExampleDAO.bulkInsertRandomAccountData:\n    '%s'\n", pstmt.toString());
//                    System.out.printf("    => %s row(s) updated in this batch\n", count.length);
//                }
//                connection.commit();
//            } catch (SQLException e) {
//                System.out.printf("BasicExampleDAO.bulkInsertRandomAccountData ERROR: { state => %s, cause => %s, message => %s }\n",
//                        e.getSQLState(), e.getCause(), e.getMessage());
//            }
//        } catch (SQLException e) {
//            System.out.printf("BasicExampleDAO.bulkInsertRandomAccountData ERROR: { state => %s, cause => %s, message => %s }\n",
//                    e.getSQLState(), e.getCause(), e.getMessage());
//        }
//        return totalNewAccounts;
//    }


//    /**
//     * Read out a subset of accounts from the data store.
//     *
//     * @param limit (int)
//     * @return Number of accounts read (int)
//     */
//    public int readAccounts(int limit) {
//        return runSQL("SELECT id, balance FROM accounts LIMIT ?", Integer.toString(limit));
//    }

//    /**
//     * Create the accounts table if it doesn't already exist.
//     *
//     */
//    public void createAccountsTable() {
//        runSQL("CREATE TABLE IF NOT EXISTS accounts (id UUID PRIMARY KEY, balance int8)");
//    }


//    /**
//     * Transfer funds between one account and another.  Handles
//     * transaction retries in case of conflict automatically on the
//     * backend.
//     * @param fromId (UUID)
//     * @param toId (UUID)
//     * @param amount (int)
//     * @return The number of updated accounts (int)
//     */
//    public int transferFunds(UUID fromId, UUID toId, BigDecimal amount) {
//        String sFromId = fromId.toString();
//        String sToId = toId.toString();
//        String sAmount = amount.toPlainString();
//
//        // We have omitted explicit BEGIN/COMMIT statements for
//        // brevity.  Individual statements are treated as implicit
//        // transactions by CockroachDB (see
//        // https://www.cockroachlabs.com/docs/stable/transactions.html#individual-statements).
//
//        String sqlCode = "UPSERT INTO accounts (id, balance) VALUES" +
//                "(?, ((SELECT balance FROM accounts WHERE id = ?) - ?))," +
//                "(?, ((SELECT balance FROM accounts WHERE id = ?) + ?))";
//
//        return runSQL(sqlCode, sFromId, sFromId, sAmount, sToId, sToId, sAmount);
//    }