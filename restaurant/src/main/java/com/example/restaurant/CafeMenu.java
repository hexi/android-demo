package com.example.restaurant;

/**
 * Created by hexi on 16/3/12.
 */
public class CafeMenu extends RestaurantMenu {
    public CafeMenu() {
        super("Cafe menu", "Dinner");

        addItem("Veggie burger and air fries",
                "Veggie burger on a whole wheat bun, lettuce, tomato, and fries",
                true,
                3.99);
        addItem("Soup of the day",
                "A cup of the soup of the day, with a side salad",
                false,
                3.69);
        addItem("Burrito",
                "A large burrito, with whole pinto beans, salsa, guacamole",
                true,
                4.29);
    }
}
