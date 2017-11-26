package com.example.killian.gpsgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
        //Database.testDatabase(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent();
        switch(position) {
            case 0:
                intent.setClass(this, WorkoutActivity.class);
                startActivity(intent);
                break;
            case 1:
                Toast.makeText(getBaseContext(), "Not implemented", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getBaseContext(), "Not implemented", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}