package com.example.hexi.canvastest.model;

import java.util.ArrayList;

/**
 * Created by zhiguo.jiang on 16/7/5.
 * 新的登录结果
 */
public class LoginInfoResult {
    public int code;
    public String msg;
    public String token;
    public ArrayList datas;
    public boolean success;
    public User user;
    public ArrayList<MultiAcc> multiAccs;
    public Tag tag;

    public class Tag {
        public String frontTag;
    }



    /**
     * 获取当前的frontTag
     */
    public String getFrontTag() {
        if (tag != null) {
            return tag.frontTag;
        }
        return null;
    }

    public enum BusinessType {
        TT_B("天津贵金属交易所(大盘)"), YG_M("广东贵金属交易中心(中盘)"), YG_B("广东贵金属交易中心(大盘)");

        BusinessType(String name) {
            this.marketName = name;
        }

        public String marketName;
    }
}
