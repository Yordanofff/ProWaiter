package BackEnd.Restaurant;

import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Menu.RestaurantMenu;
import FrontEnd.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<Dish> orderedDishes;
    private double totalPrice = 0;

    // Keep incrementing the number on each new order, so we have total number of orders.
    private static int orderNumber = 0;
    boolean isPaid;

    public Order() {
        orderNumber += 1;
        this.isPaid = false;
        this.orderedDishes = new ArrayList<>();
    }

    public void addDish(Dish dish) {
        if (!isDishInMenu(dish)) {
            ConsolePrinter.printWarning("The item [" + dish.getName() + "] is not in the Restaurant Menu!");
        }
        orderedDishes.add(dish);
        setTotalPrice(getTotalPrice() + dish.getPrice());
    }

    public void removeDish(Dish dish) {
        orderedDishes.remove(dish);
        setTotalPrice(getTotalPrice() - dish.getPrice());
    }

    public List<Dish> getOrderedDishes() {
        return orderedDishes;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    private void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    private boolean isDishInMenu(Dish dish){
        List<Dish> allRestaurantMenuDishes = RestaurantMenu.getDishes();
        for (Dish restaurantDish: allRestaurantMenuDishes             ) {
            if (restaurantDish.getName().equalsIgnoreCase(dish.getName())) {
                return true;
            }
        }
        return false;
    }
}
