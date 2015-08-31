package com.example.hexi.toolbar;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by hexi on 15/8/31.
 */
public class NavigationActivity extends AppCompatActivity implements NavigationFragment.NavigationDrawerCallbacks {

    private static final String TAG = "NavigationActivity";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("首页");
            setSupportActionBar(toolbar);
            // Set Navigation Toggle
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        // 安装 drawer toggle 并放入 toolbar
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        LollipopUtils.setStatusbarColor(this, findViewById(R.id.rl_content));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_setting : {
                Log.d(TAG, "===setting clicked===");
                return true;
            }
            case R.id.menu_item_login : {
                Log.d(TAG, "===login clicked===");
                return true;
            }
            default: {
                return true;
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        changeFragment(position);
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }

    }

    private void changeFragment(int position) {
        Log.d(TAG, "===changeFragment, postion: " + position);
        if (position == 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ViewPagerfragment()).commit();
        } else if (position == 1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new CardFragment()).commit();
        }
    }
}
