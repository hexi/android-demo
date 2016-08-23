package com.example.hexi.canvastest.model;

import android.util.Log;

import java.util.Date;

/**
 * Created by rjhy on 14-12-5.
 */
public class WarningSetting implements Cloneable {
    private static final String TAG = "WarningSetting";

    public long id;
    public String username;
    public String deviceId;
    public String sid;
    public long dtCreateTime;
    public double priceTarget;
    public long dtUpdateTime;
    public String direction;
    public double priceNow;
    public boolean isOpen;

    public WarningSetting() {
    }

    public WarningSetting(double priceTarget, String deviceId, double priceNow, String sid) {
        this.priceTarget= priceTarget;
        this.deviceId = deviceId;
        this.dtUpdateTime = new Date().getTime();
        this.isOpen = true;
        this.priceNow = priceNow;
        this.sid = sid;
    }

    public WarningSetting copy() {
        try {
            return (WarningSetting) super.clone();
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "copy error", e);
            return null;
        }
    }

    private boolean editing;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public long getDtCreateTime() {
        return dtCreateTime;
    }

    public void setDtCreateTime(long dtCreateTime) {
        this.dtCreateTime = dtCreateTime;
    }

    public double getPriceTarget() {
        return priceTarget;
    }

    public void setPriceTarget(double priceTarget) {
        this.priceTarget = priceTarget;
    }

    public long getDtUpdateTime() {
        return dtUpdateTime;
    }

    public void setDtUpdateTime(long dtUpdateTime) {
        this.dtUpdateTime = dtUpdateTime;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getPriceNow() {
        return priceNow;
    }

    public void setPriceNow(double priceNow) {
        this.priceNow = priceNow;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }
}
