package com.android.example.animationdemo.objectAnimation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.example.animationdemo.R;
import com.android.example.animationdemo.objectAnimation.data.Person;

/**
 * Created by hexi on 16/9/4.
 */
public class AnimationNumberActivity extends Activity implements ValueAnimator.AnimatorUpdateListener {
    Person person;
    TextView personInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_number_animation);
        personInfoView = (TextView) findViewById(R.id.person_info);
        person = new Person("张三", 1);
    }

    public void changeAge(View view) {
        ObjectAnimator animator = ObjectAnimator
                .ofInt(person, "age", 1, 100)
                .setDuration(5000);
        animator.addUpdateListener(this);
        animator.start();

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        Log.d("AnimationNumber",
                String.format("animated value:%d, age:%d", animation.getAnimatedValue(), person.getAge()));

        int values = (int) animation.getAnimatedValue();
        person.setAge(values);
        personInfoView.setText(person.toString());
    }
}
