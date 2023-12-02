package BackEnd.Restaurant;

import BackEnd.DB.DBOperations;

import java.util.List;

public class Table {
    private int tableNumber;
    private Order currentOrder;
    private boolean isOccupied;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
        this.currentOrder = null;  // TODO: Make changes here to load the current order from DB..
        this.isOccupied = false;
    }

    public Table(int tableNumber, boolean isOccupied){
        this.tableNumber = tableNumber;
        this.isOccupied = isOccupied;
        this.currentOrder = null;
    }

//    public static Order loadOrderFromDB() {
//      // TODO: To be implemented.
//    }
//
//    public static void saveOrderToDB(Order order) {
//      // TODO: I think I already created this method somewhere - don't add it here for now.
//    }

    @Override
    public String toString() {
        return "Table{" +
                "tableNumber=" + tableNumber +
                ", currentOrder=" + currentOrder +
                ", isOccupied=" + isOccupied +
                "} \n";
    }

    public static List<Table> getTablesFromDB(){
        return DBOperations.getAllTablesFromDB();
    }

    public void freeUpTable() {
        unOccupy();
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void assignOrder(Order order) {
        this.currentOrder = order;
    }

    private void clearOrder() {
        this.currentOrder = null;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
        // TODO: get from DB ? - to make sure another person hasn't changed it?
    }

    public void occupy() {
        isOccupied = true;
        DBOperations.updateOccupyTable(this);
    }

    public void unOccupy() {
        isOccupied = false;
        DBOperations.updateOccupyTable(this);
    }
}
