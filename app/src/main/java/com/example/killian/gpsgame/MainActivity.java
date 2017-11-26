package com.example.killian.gpsgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by kev on 26/11/17.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    ListView choiceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        choiceList = (ListView) findViewById(R.id.mainChoicesListView);
        choiceList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent();
        switch(position) {
            case 0:
                intent.setClass(this, WorkoutActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("id", id);
                startActivity(intent);
        }
    }
}