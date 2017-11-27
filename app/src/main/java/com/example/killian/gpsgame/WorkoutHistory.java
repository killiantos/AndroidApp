package com.example.killian.gpsgame;

/**
 * Created by brian on 26/11/17.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutHistory extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView runList;
    private ArrayList<String> previousRuns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);
        getRuns();
        runList = (ListView) findViewById(R.id.runListView);

        ArrayAdapter<String> runAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, previousRuns);

        runList.setAdapter(runAdapter);
        runList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(getBaseContext(), "Not implemented", Toast.LENGTH_SHORT).show();
    }

    private void getRuns() {
        try {
            /* NEED TO FIGURE OUT HOW TO ACCESS DATABASE IN ACTIVITY
            previousRuns = database.get(); */
            previousRuns = new ArrayList<>();
            for(int i=0; i<20; i++) {
                String run = "Run " + (i+1) + ", " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date());
                previousRuns.add(run);
            }
        }
        catch(Exception e) {
            System.out.println("ERROR: Problem occurred when fetching previous run data.");
            e.printStackTrace();
        }
    }

}
