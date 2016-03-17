package com.example.restaurant;

import com.example.restaurant.model.Menu;
import com.example.restaurant.model.MenuComponent;
import com.example.restaurant.model.MenuItem;

/**
 * Created by hexi on 16/3/12.
 */
public abstract class RestaurantMenu {
    private MenuComponent menu;

    public RestaurantMenu(String name, String description) {
        menu = new Menu(name, description);
    }

    public void addItem(String name, String description,
                        boolean vegetarian, double price) {
        menu.add(new MenuItem(name, description, vegetarian, price));
    }

    public MenuComponent getMenu() {
        return menu;
    }
}
