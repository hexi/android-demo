package com.example.restaurant.test;

import com.example.restaurant.CafeMenu;
import com.example.restaurant.DessertMenu;
import com.example.restaurant.DinerMenu;
import com.example.restaurant.PancakeHouseMenu;
import com.example.restaurant.client.Waitress;
import com.example.restaurant.model.Menu;
import com.example.restaurant.model.MenuComponent;

public class MenuTestDrive {

    public static void main(String[] args) {
        MenuComponent pancakeHouseMenu = new PancakeHouseMenu().getMenu();
        MenuComponent dinerMenu = new DinerMenu().getMenu();
        MenuComponent cafeMenu = new CafeMenu().getMenu();
        MenuComponent dessertMenu = new DessertMenu().getMenu();

        MenuComponent allMenes = new Menu("All menus", "All menus combined");

        dinerMenu.add(dessertMenu);

//        allMenes.add(pancakeHouseMenu);
        allMenes.add(dinerMenu);
//        allMenes.add(cafeMenu);

        Waitress waitress = new Waitress(allMenes);

        waitress.printMenu();
        waitress.printVegetarianMenu();
    }
}
