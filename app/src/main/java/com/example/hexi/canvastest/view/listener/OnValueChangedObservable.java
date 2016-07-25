package com.example.hexi.canvastest.view.listener;

/**
 * Created by hexi on 16/7/7.
 */
public interface OnValueChangedObservable {

    public void addOnValueChangedListener(OnValueChangedListener listener);

    public void removeOnValueChangedListener(OnValueChangedListener listener);

    public double getValue();

    public void onValueChanged(double value);
}
