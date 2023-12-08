package BackEnd.Restaurant.Dishes;

public class OrderedDish {
    // Created so that we can add more than one dish in the same time.
    private Dish dish;
    private int quantity;

    @Override
    public String toString() {
        return "OrderedDish{" +
                "dish=" + dish +
                ", quantity=" + quantity +
                '}';
    }

    public OrderedDish(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity(int additionalQuantity) {
        this.quantity += additionalQuantity;
    }
}
