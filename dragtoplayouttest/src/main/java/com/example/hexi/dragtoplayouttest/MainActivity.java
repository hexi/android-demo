package com.example.hexi.dragtoplayouttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.chenupt.dragtoplayout.AttachUtil;
import github.chenupt.dragtoplayout.DragTopLayout;

public class MainActivity extends AppCompatActivity {
    DragTopLayout dragTopLayout;
    View topViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dragTopLayout = (DragTopLayout)findViewById(R.id.drag_top_layout);
        topViewContainer = findViewById(R.id.top_view_container);

        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(new SimpleAdapter(this,
                mockData(),
                android.R.layout.simple_list_item_1,
                new String[]{"name"},
                new int[]{android.R.id.text1}));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                dragTopLayout.setTouchMode(AttachUtil.isAdapterViewAttach(view));
            }
        });

    }

    private List<? extends Map<String, ?>> mockData() {
        List<Map<String, String>> result = new ArrayList<>();
        for (int i =0; i < 20; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("name", "你猜猜我是谁"+i);
            result.add(map);
        }
        return result;
    }
}
