package BackEnd.Restaurant.Dishes;

public class Dessert extends Dish{
    private static final DishType dishType = DishType.DESSERT;

    public Dessert(String name, double price) {
        super(name, price, dishType);
    }
}
