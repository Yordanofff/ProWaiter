package BackEnd.Restaurant.Dishes;

public class Dish {
    // quantity will always be 1.
    private String name;
    private double price;
    private DishType dishType;

    public Dish(String name, double price, DishType dishType) {
        this.name = name;
        this.price = price;
        this.dishType = dishType;
    }

    public Dish(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Dish(DishType dishType) {
        this.dishType = dishType;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", dishType=" + dishType +
                "} \n";
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

    public DishType getDishType() {
        return dishType;
    }

    public static String[] getDishTypeNames() {
        DishType[] dishTypes = DishType.values();

        String[] dishTypeNames = new String[dishTypes.length];

        // Populate the String array with enum names
        for (int i = 0; i < dishTypes.length; i++) {
            dishTypeNames[i] = dishTypes[i].name();
        }
        return dishTypeNames;
    }
}
