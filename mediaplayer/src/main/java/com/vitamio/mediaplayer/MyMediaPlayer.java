package com.vitamio.mediaplayer;

import com.pili.pldroid.player.PLMediaPlayer;

/**
 * Created by hexi on 16/3/24.
 */
public class MyMediaPlayer extends PLMediaPlayer {

    private boolean prepared;

    public boolean isPrepared() {
        return prepared;
    }

    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }

}
