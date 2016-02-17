package com.baidao.realmtest.model;

import io.realm.RealmObject;

/**
 * Created by hexi on 16/2/17.
 */
public class Cat extends RealmObject {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
