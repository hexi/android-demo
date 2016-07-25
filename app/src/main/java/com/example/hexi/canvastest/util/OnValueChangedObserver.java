package com.example.hexi.canvastest.util;


import com.example.hexi.canvastest.view.listener.OnValueChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hexi on 16/7/7.
 */
public class OnValueChangedObserver {

    private List<OnValueChangedListener> listeners = new ArrayList<>();

    public void addOnValueChangedListener(OnValueChangedListener listener) {
        if (listener == null) {
            return;
        }
        this.listeners.add(listener);
    }

    public void removeOnValueChangedListener(OnValueChangedListener listener) {
        if (listener == null) {
            return;
        }
        this.listeners.remove(listener);
    }

    public void notifyValueChanged(double value) {
        for (OnValueChangedListener listener : listeners) {
            if (listener == null) {
                continue;
            }
            listener.onValueChanged(value);
        }
    }
}
