package BackEnd.Restaurant;

import BackEnd.DB.DBOperations;
import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Dishes.OrderedDish;
import BackEnd.Restaurant.Menu.RestaurantMenu;
import FrontEnd.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<OrderedDish> orderedDishes;
    private double totalPrice = 0;
    boolean isPaid;
    private OrderStatus orderStatus;
    private int tableNumber;
    private long orderNumber;  // Very long in the DB. Int might not be long enough. Will be created from the DB.

    public Order(Table table) {
        this.isPaid = false;
        this.orderedDishes = new ArrayList<>();
        this.orderStatus = OrderStatus.CREATED;
        this.tableNumber = table.getTableNumber();
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    // From DB;
    public Order(long orderNumber, int tableNumber, boolean isPaid, OrderStatus orderStatus) {
        this.orderNumber = orderNumber;
        this.tableNumber = tableNumber;
        this.isPaid = isPaid;
        this.orderStatus = orderStatus;
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

    public void addDish(OrderedDish dish) {
        if (!isDishInMenu(dish.getDish())) {
            ConsolePrinter.printWarning("The item [" + dish.getDish().getName() + "] is not in the Restaurant Menu!");
        }
        orderedDishes.add(dish);
        setTotalPrice(getTotalPrice() + dish.getDish().getPrice() * dish.getQuantity());
    }

    public void removeDish(Dish dish) {
        orderedDishes.remove(dish);
        setTotalPrice(getTotalPrice() - dish.getPrice());
    }

    public List<OrderedDish> getOrderedDishes() {
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        // Don't allow setting up the order status back to "Created" if it's already something else.
        if ((getOrderStatus() != OrderStatus.CREATED) && (orderStatus == OrderStatus.CREATED)) {
            ConsolePrinter.printError("You can't set the order status back to [CREATED]!");
            return;
        }
        this.orderStatus = orderStatus;
    }
}
