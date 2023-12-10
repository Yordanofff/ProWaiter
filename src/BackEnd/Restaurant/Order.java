package BackEnd.Restaurant;

import BackEnd.DB.DBOperations;
import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Dishes.OrderedDish;
import BackEnd.Restaurant.Menu.RestaurantMenu;
import FrontEnd.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import static FrontEnd.Validators.formatDecimalNumber;

public class Order {
    private List<OrderedDish> orderedDishes;
    private double totalPrice = 0;
    private final boolean isPaid;
    private OrderStatus orderStatus;
    private final int tableNumber;
    private long orderNumber;  // Very long in the DB. Int might not be long enough. Will be created from the DB.
    private final LocalDateTime creationDateTime;

    public Order(Table table) {
        this.isPaid = false;
        this.orderedDishes = new ArrayList<>();
        this.orderStatus = OrderStatus.CREATED;
        this.tableNumber = table.getTableNumber();
        this.creationDateTime = LocalDateTime.now();
    }

    // From DB;
    public Order(long orderNumber, int tableNumber, boolean isPaid, OrderStatus orderStatus, LocalDateTime creationDateTime) {
        this.orderNumber = orderNumber;
        this.tableNumber = tableNumber;
        this.isPaid = isPaid;
        this.orderStatus = orderStatus;
        this.creationDateTime = creationDateTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderedDishes=" + orderedDishes +
                ", totalPrice=" + totalPrice +
                ", isPaid=" + isPaid +
                ", orderStatus=" + orderStatus +
                ", tableNumber=" + tableNumber +
                '}';
    }

    public void addOrderedDish(OrderedDish orderedDish) {
        if (!isDishInMenu(orderedDish.getDish())) {
            ConsolePrinter.printWarning("The item [" + orderedDish.getDish().getName() + "] is not in the Restaurant Menu!");
        }

        setOrderedDishes(getOrderedDishesFromDB());
        orderedDishes.add(orderedDish);

        DBOperations.updateOrderDishesToDB(this);
        setTotalPrice(getTotalPrice() + orderedDish.getDish().getPrice() * orderedDish.getQuantity());
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * There might be multiple rows with the same dish name if the items weren't ordered in the same time.
     * If we are trying to remove 20 items, and the current row has 5 - remove all 5 (don't add the row to the new list)
     * and keep looking for the remaining 15. Don't attempt to remove the OrderedDish from the original list, or it will
     * crash with "java.util.ConcurrentModificationException".
     *
     * @param dishNameToRemove  - the name of the Dish that we want to remove
     * @param numDishesToRemove - number of that dish that we want to remove
     */
    public void removeOrderedDish(String dishNameToRemove, int numDishesToRemove) {
        List<OrderedDish> currentOrderDishes = getOrderedDishesFromDB();
        List<OrderedDish> editedOrder = new ArrayList<>();

        printWarningIfTryingToRemoveMoreThanWhatsBeenOrdered(dishNameToRemove, numDishesToRemove);

        boolean isMoreToRemove = true;
        // The item will be 100% in the list - because it's selection (no need to check)
        for (OrderedDish dishInCurrentOrder : currentOrderDishes) {
            if (dishInCurrentOrder.getDish().getName().equalsIgnoreCase(dishNameToRemove)) {
                if (isMoreToRemove) {
                    int currentOrderDishQuantity = dishInCurrentOrder.getQuantity();

                    if (currentOrderDishQuantity > numDishesToRemove) {
                        dishInCurrentOrder.setQuantity(currentOrderDishQuantity - numDishesToRemove);
                        editedOrder.add(dishInCurrentOrder);  // with less quantity.
                    } else {
                        // Just don't add the dish to the new edited list.
                        numDishesToRemove -= currentOrderDishQuantity;
                    }

                    if (numDishesToRemove == 0) {
                        isMoreToRemove = false;
                    }
                } else {
                    editedOrder.add(dishInCurrentOrder);
                }
            } else {
                editedOrder.add(dishInCurrentOrder);
            }
        }

        // Set the new list as a new OrderedDishes list and update the DB.
        setOrderedDishes(editedOrder);
        DBOperations.updateOrderDishesToDB(this);

        setTotalPrice(getCalculatedTotalPrice());
    }

    public void printWarningIfTryingToRemoveMoreThanWhatsBeenOrdered(String dishNameToRemove, int numDishesToRemove) {
        int totalNumberOfOrderedDishName = getTotalNumberOfOrderedDishName(dishNameToRemove);
        if (numDishesToRemove > totalNumberOfOrderedDishName) {
            ConsolePrinter.printWarning("You are trying to remove [" + numDishesToRemove + " x " + dishNameToRemove + "  ]. There are total of [" + totalNumberOfOrderedDishName + "] dishes and all will be removed.");
        }
    }

    public int getTotalNumberOfOrderedDishName(String dishName) {
        List<OrderedDish> currentOrder = getOrderedDishesFromDB();
        int total = 0;
        for (OrderedDish orderedDish : currentOrder) {
            if (orderedDish.getDish().getName().equalsIgnoreCase(dishName)) {
                total += orderedDish.getQuantity();
            }
        }
        return total;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setOrderedDishes(List<OrderedDish> orderedDishes) {
        this.orderedDishes = orderedDishes;
    }

    public List<OrderedDish> getOrderedDishesLocal() {
        // Retrieve from DB.
        return this.orderedDishes;
    }

    public List<OrderedDish> getOrderedDishesFromDB() {
        // Retrieve from DB.
        return DBOperations.getOrdersDishesForTableNumber(this.tableNumber);
    }

    public long getOrderNumber() {
        if (orderNumber != 0) {
            return orderNumber;
        }
        return DBOperations.getOrderIDOfOccupiedTable(tableNumber);
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public double getCalculatedTotalPrice() {
        List<OrderedDish> currentOrder = getOrderedDishesFromDB();
        double totalPrice = 0;
        for (OrderedDish orderedDish : currentOrder) {
            totalPrice += orderedDish.getDish().getPrice() * orderedDish.getQuantity();
        }
        return totalPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    private void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    private boolean isDishInMenu(Dish dish) {
        List<Dish> allRestaurantMenuDishes = RestaurantMenu.getDishes();
        for (Dish restaurantDish : allRestaurantMenuDishes) {
            if (restaurantDish.getName().equalsIgnoreCase(dish.getName())) {
                return true;
            }
        }
        return false;
    }

    public OrderStatus getOrderStatusLocal() {
        return orderStatus;
    }

    public void setOrderStatusAndSaveToDB(OrderStatus orderStatus) {
        // Don't allow setting up the order status back to "Created" if it's already something else.
        if ((getOrderStatusLocal() != OrderStatus.CREATED) && (orderStatus == OrderStatus.CREATED)) {
            ConsolePrinter.printError("You can't set the order status back to [CREATED]!");
            return;
        }
        this.orderStatus = orderStatus;
        DBOperations.updateOrderStatus(this);
    }

    public void printCurrentOrder() {
        List<OrderedDish> orderedDishes = getOrderedDishesFromDB();
        for (OrderedDish d : orderedDishes) {
            System.out.println("Ordered " + d.getQuantity() + " x " + d.getDish().getName() + " - " +
                    formatDecimalNumber(d.getDish().getPrice()) +
                    " Total: " + formatDecimalNumber(d.getDish().getPrice() * d.getQuantity()));
        }
        System.out.println("Total: " + formatDecimalNumber(getCalculatedTotalPrice()));
    }
}
