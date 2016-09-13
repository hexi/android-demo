package com.android.example.animationdemo.objectAnimation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.example.animationdemo.R;

/**
 * Created by hexi on 16/8/20.
 */
public class BaseObjectAnimActivity extends AppCompatActivity {

    private static final String TAT = "ObjectRotationActivity";
    ImageView imageView;
    View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_object_anim);
        contentView = findViewById(R.id.content);
        imageView = (ImageView) findViewById(R.id.show);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_base_object_anim, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ObjectAnimator objectAnimator;
        float currentX;
        switch (item.getItemId()) {
            case R.id.scale:
                Log.d(TAT, "===scale===");
                objectAnimator = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 2f,1f);
                objectAnimator.setDuration(3000);
                objectAnimator.start();
                return true;
            case R.id.translation:
                int width = contentView.getMeasuredWidth();
                currentX = imageView.getTranslationX();
                int left = imageView.getLeft();
                int delta = width - left - imageView.getMeasuredWidth();
                Log.d(TAT, String.format("===translation, Width:%d, transX:%f", width, currentX));
//                objectAnimator = ObjectAnimator.ofFloat(imageView, "translationX", currentX, 500, currentX);
                objectAnimator = ObjectAnimator.ofFloat(imageView, "translationX", currentX, delta, currentX);
                objectAnimator.setDuration(1000);
                objectAnimator.start();
                return true;
            case R.id.rotation:
                Log.d(TAT, "===rotation===");
                objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
                objectAnimator.setDuration(3000);
                objectAnimator.start();
                return true;
            case R.id.alpha:
                Log.d(TAT, "===alpha===");
                objectAnimator = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f, 1f);
                objectAnimator.setDuration(3000);
                objectAnimator.start();
                return true;
            case R.id.combine:
                currentX = imageView.getTranslationX();
                ObjectAnimator translation = ObjectAnimator.ofFloat(imageView, "translationX", currentX, 500, currentX);
                ObjectAnimator rotation = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f,1f);
                ObjectAnimator scale = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 3f, 1f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(translation)
                        .after(rotation)
                        .with(alpha)
                        .with(scale);
                animatorSet.setDuration(5000);
                animatorSet.start();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
