package BackEnd.Restaurant;

public class Drink extends Dish{
    public static final DishType dishType = DishType.DRINK;

    public Drink() {
        this.setDishType(dishType);
    }
}
