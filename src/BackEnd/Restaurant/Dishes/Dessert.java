package BackEnd.Restaurant.Dishes;

public class Dessert extends Dish{
    public static final DishType dishType = DishType.DESSERT;
    public Dessert() {
        super(dishType);
    }

    public Dessert(String name, double price) {
        super(name, price, dishType);
    }
}
