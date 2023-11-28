package BackEnd.Restaurant;

import BackEnd.Users.User;
import FrontEnd.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private List<Table> tables;
    private List<User> users;  // todo ?

    public Restaurant(int numberOfTables) {
        tables = new ArrayList<>();
        for (int i = 1; i <= numberOfTables; i++) {
            tables.add(new Table(i));
        }
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
        for (Table table : tables) {
            if (!table.isOccupied()) {
                freeTables.add(table);
            }
        }
        return freeTables;
    }

    public List<Table> getOccupiedTables() {
        List<Table> occupiedTables = null;
        for (Table table : tables) {
            if (table.isOccupied()) {
                occupiedTables.add(table);
            }
        }
        return occupiedTables;
    }
}
