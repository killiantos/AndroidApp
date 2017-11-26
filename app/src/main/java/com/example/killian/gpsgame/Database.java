package com.example.killian.gpsgame;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by carlm on 14/11//2017.
 */

public class Database extends SQLiteOpenHelper {
    public Database(Context context) {
        super(context, "runlog.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE run_info " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "distance INTEGER, " +
                "steps INTEGER)"
        );
        sqLiteDatabase.execSQL("CREATE TABLE waypoint_info " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "run_id INTEGER REFERENCES run_info(id) ON UPDATE CASCADE, " +
                "xcoord INTEGER, " +
                "ycoord INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS run_info");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS waypoint_info");
        onCreate(sqLiteDatabase);
    }

    public void reset() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS run_info");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS waypoint_info");
        onCreate(sqLiteDatabase);
    }

    public boolean insert(Run run) {
        ContentValues run_info = new ContentValues();
        run_info.put("date", run.date);
        run_info.put("distance", new String("" + run.distance));
        run_info.put("steps", new String("" + run.steps));

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long id = sqLiteDatabase.insert("run_info", null, run_info);
        if(id == -1){
            return false;
        }

        for(Run.Waypoint waypoint : run.waypoints) {
            ContentValues waypoint_info = new ContentValues();
            waypoint_info.put("run_id", new String("" + id));
            waypoint_info.put("xcoord", new String("" + waypoint.x));
            waypoint_info.put("ycoord", new String("" + waypoint.y));

            sqLiteDatabase.insert("waypoint_info", null, waypoint_info);
        }

        return true;
    }

    public ArrayList<Run> get() {
        ArrayList<Run> run_list = new ArrayList<Run>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor run_info = sqLiteDatabase.rawQuery("SELECT * FROM run_info", null);

        while(run_info.moveToNext()) {
            long id = Integer.parseInt(run_info.getString(0));

            Run run = new Run();
            run.date = run_info.getString(1);
            run.distance = Integer.parseInt(run_info.getString(2));
            run.steps = Integer.parseInt(run_info.getString(3));

            Cursor waypoint_info = sqLiteDatabase.rawQuery("SELECT * FROM waypoint_info WHERE run_id=" + id, null);
            while(waypoint_info.moveToNext()) {
                int x = Integer.parseInt(waypoint_info.getString(2));
                int y = Integer.parseInt(waypoint_info.getString(3));

                run.addWaypoint(x, y);
            }

            run_list.add(run);
        }

        return run_list;
    }

    static void testDatabase(AppCompatActivity activity) {
        Database database = new Database(activity);

        Run run_data = new Run();
        run_data.date = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date());
        run_data.distance = 1;
        run_data.steps = 200;
        for(int i = 0; i < 5; i++) {
            run_data.addWaypoint(i, i);
        }

        if(!database.insert(run_data)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(true);
            alert.setTitle("MyApplication Database");
            alert.setMessage("Failed to insert data");
            alert.show();
        }

        ArrayList<Run> data = database.get();
        if(data.size() != 0) {
            StringBuffer buffer = new StringBuffer();
            for(Run run : data) {
                buffer.append("date: " + run.date + "\n");
                buffer.append("distance: " + run.distance + "\n");
                buffer.append("steps: " + run.steps + "\n");
                buffer.append("waypoints:\n");
                for(Run.Waypoint waypoint : run.waypoints) {
                    buffer.append("\t(" + waypoint.x +", " + waypoint.y + ")\n");
                }
                buffer.append("\n");
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(true);
            alert.setTitle("MyApplication Database");
            alert.setMessage(buffer.toString());
            alert.show();
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(true);
            alert.setTitle("MyApplication Database");
            alert.setMessage("No data retrieved");
            alert.show();
        }
    }
}