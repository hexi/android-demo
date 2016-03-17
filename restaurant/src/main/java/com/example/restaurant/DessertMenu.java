package com.example.restaurant;

/**
 * Created by hexi on 16/3/12.
 */
public class DessertMenu extends RestaurantMenu {

    public DessertMenu() {
        super("Dessert menu", "Dessert of course!");

        addItem("Apple Pie",
                "Apple pie with a flakey crust, topped with vanilla ice cream",
                true,
                1.59);
        addItem("Cheesecake",
                "Creamy new york cheesecake, with a chocolate graham crust",
                true,
                1.99);
        addItem("Sorbet",
                "A scoop of raspberry and a scoop of lime",
                false,
                1.89);
    }
}
