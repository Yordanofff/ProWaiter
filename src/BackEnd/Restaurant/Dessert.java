package BackEnd.Restaurant;

public class Dessert extends Dish{
    public static final DishType dishType = DishType.DESSERT;
    public Dessert() {
        this.setDishType(dishType);
    }
}
