package com.example.restaurant.iterator;

import com.example.restaurant.model.MenuComponent;

import java.util.Iterator;

/**
 * Created by hexi on 16/3/12.
 */
public class NullIterator implements Iterator<MenuComponent> {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public MenuComponent next() {
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
