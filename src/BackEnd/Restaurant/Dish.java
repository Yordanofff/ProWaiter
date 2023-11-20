package BackEnd.Restaurant;

public class Dish {
    // quantity will always be 1.
    private String name;
    private double price;
    private DishType dishType;
    private int size;  // todo - grams for food ? ml for drinks? Not added in the Constructor

    public Dish(String name, double price, DishType dishType) {
        this.name = name;
        this.price = price;
        this.dishType = dishType;
    }

    public Dish(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public DishType getDishType() {
        return dishType;
    }

    public void setDishType(DishType dishType) {
        this.dishType = dishType;
    }
}
