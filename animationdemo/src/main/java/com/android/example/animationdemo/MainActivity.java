package com.android.example.animationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.example.animationdemo.objectAnimation.AnimationNumberActivity;
import com.android.example.animationdemo.objectAnimation.BaseObjectAnimActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.base_object_anim:
                startActivity(new Intent(this, BaseObjectAnimActivity.class));
                return true;

            case R.id.animation_number:
                startActivity(new Intent(this, AnimationNumberActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
