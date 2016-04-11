package com.example.hexi.canvastest;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

public class AnimationTest extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_test);

        ImageView imageView = (ImageView) findViewById(R.id.iv_animation);
        ((Animatable)imageView.getDrawable()).start();
    }

}
