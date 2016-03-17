package com.example.restaurant;

/**
 * Created by hexi on 16/3/12.
 */
public class PancakeHouseMenu extends RestaurantMenu {

    public PancakeHouseMenu() {
        super("Pancake house menu", "Breakfast");
        addItem("K&B's pancake Breakfast",
                "Pancakes with scrambled eggs, and toast",
                true,
                2.99);
        addItem("Regular Pancake Breakfast",
                "Pancakes with fried eggs, sausage",
                false,
                2.99);
        addItem("Blueberry Pancakes",
                "Pancakes made with fresh blueberries",
                true,
                3.49);

        addItem("Waffles",
                "Waffles, with your choice of blueberries of strawberries",
                true,
                3.59);
    }

}
