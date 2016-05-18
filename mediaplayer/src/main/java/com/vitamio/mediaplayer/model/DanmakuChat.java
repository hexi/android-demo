package com.vitamio.mediaplayer.model;

/**
 * Created by hexi on 16/5/13.
 */
public class DanmakuChat {
    public boolean isLive = true;
    public String username;
    public String content;

    public DanmakuChat(String username, String content) {
        this.username = username;
        this.content = content;
    }
}
