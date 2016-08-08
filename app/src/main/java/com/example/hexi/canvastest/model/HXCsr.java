package com.example.hexi.canvastest.model;

import com.google.gson.Gson;

/**
 * Created by chengxin on 2/22/16.
 */
public class HXCsr {
    private long id;
    private String avatar;

    private String nickname;

    public HXCsr() {}

    public HXCsr(long id, String avatar, String nickname) {
        this.id = id;
        this.avatar = avatar;
        this.nickname = nickname;
    }

    public long getCsrId() {
        return id;
    }

    public String getHXID() {
        return "CRM" + id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
