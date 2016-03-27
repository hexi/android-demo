package com.vitamio.mediaplayer;

/**
 * Created by hexi on 16/3/24.
 */
public class MyMediaPlayer extends android.media.MediaPlayer {

    private boolean prepared;

    public boolean isPrepared() {
        return prepared;
    }

    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }

}
