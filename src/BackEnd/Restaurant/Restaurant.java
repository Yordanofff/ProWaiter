package BackEnd.Restaurant;

import BackEnd.Users.User;
import BackEnd.Users.UserManager;
import FrontEnd.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private RestaurantInfo restaurantInfo = loadRestaurantInfo();
    private List<Table> tables = initTables() ;//= new ArrayList<>();
//    private static List<User> users = new ArrayList<>();

//    public Restaurant() {
//        loadRestaurantInfo();
//        initTables();
//    }

    public RestaurantInfo loadRestaurantInfo() {
        // Load restaurantInfo from DB. If not in DB - Ask User + write to DB.
        RestaurantInfo restaurantInfoDB = RestaurantInfo.getRestaurantInfoFromDB();
        if (restaurantInfoDB == null) {
            ConsolePrinter.printWarning("RestaurantInfo not found in the DB.");
            RestaurantInfo newRestaurantInfo = new RestaurantInfo();
            setRestaurantInfo(newRestaurantInfo.getNewRestaurantInfoFromUser());
            return newRestaurantInfo;
        }

        ConsolePrinter.printInfo("Restaurant info loaded from DB.");
        return restaurantInfoDB;
    }

    public List<Table> initTables() {
        // Load tables from DB. If not in DB - Create new tables + write to DB.
        List<Table> tablesFromDB = Table.getTablesFromDB();
        if (tablesFromDB.isEmpty()) {
            ConsolePrinter.printWarning("Tables not found in the DB.");
            List<Table> newTableList = generateNewTables(restaurantInfo.getNumberOfTables());
            // todo - write Tables to DB
            return newTableList;
        }
        ConsolePrinter.printInfo("Tables loaded from DB.");
            return tablesFromDB;
    }

//
//    public List<Table> getTables() {
//        if (restaurantInfo == null || )
//        return tables;
//    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public static List<Table> generateNewTables(int numberOfTables) {
        List<Table> newTables = new ArrayList<>();
        for (int i = 1; i <= numberOfTables; i++) {
            newTables.add(new Table(i));
        }
        return newTables;
    }

    public Table getTable(int tableNumber) {
        if (tableNumber > tables.size()) {
            ConsolePrinter.printError("Table [" + tableNumber + "] doesn't exist. Max table number [" + tables.size() + "]");
            return null;
        }
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                return table;
            }
        }
        return null; // Table not found
    }

    public List<Table> getFreeTables() {
        List<Table> freeTables = new ArrayList<>();
        for (Table table : this.tables) {
            if (!table.isOccupied()) {
                freeTables.add(table);
            }
        }
        return freeTables;
    }

    public List<Table> getOccupiedTables() {
        List<Table> occupiedTables = new ArrayList<>();
        for (Table table : tables) {
            if (table.isOccupied()) {
                occupiedTables.add(table);
            }
        }
        return occupiedTables;
    }

    private static int[] getTables(List<Table> tablesList) {
        int[] tables = new int[tablesList.size()];
        for (int i = 0; i < tablesList.size(); i++) {
            tables[i] = tablesList.get(i).getTableNumber();
        }
        return tables;
    }

    public int[] getFreeTablesArr() {
        return getTables(getFreeTables());
    }

    public int[] getOccupiedTablesArr() {
        return getTables(getOccupiedTables());
    }

    public List<User> getUsers() {
        return UserManager.getActiveUsers();
    }

    public int getNumberOfTables() {
        return restaurantInfo.getNumberOfTables();
    }

    public String getRestaurantName() {
        return restaurantInfo.getRestaurantName();
    }

    public RestaurantInfo getRestaurantInfo() {
        return restaurantInfo;
    }

    public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
        RestaurantInfo.saveRestaurantInfoInDB(restaurantInfo);
    }

//    private static String[] getTablesString(List<Table> tablesList) {
//        String[] tables = new String [tablesList.size()];
//        for (int i = 0; i < tablesList.size(); i++) {
//            int tableNumber = tablesList.get(i).getTableNumber();
//            String tableNumberStr = "Table " + tableNumber;
//            tables[i] = tableNumberStr;
//        }
//        return tables;
//    }
//
//    public static String[] getFreeTablesArr(){
//        return getTablesString(getFreeTables());
//    }
//
//    public static String[] getOccupiedTablesArr(){
//        return getTablesString(getOccupiedTables());
//    }

}
