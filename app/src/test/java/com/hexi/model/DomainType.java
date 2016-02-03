package com.hexi.model;

/**
 * Created by hexi on 16/1/15.
 */
public enum  DomainType {
    QUOTES("quotes"),
    WWW("www"),
    JRY("jry"),
    CHAT("chat");

    public String type;

    DomainType(String type) {
        this.type = type;
    }

}
