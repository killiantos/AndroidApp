package com.example.killian.gpsgame;

/**
 * Created by Kevin on 23-Nov-17.
 */
import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;

public class StepCounter extends MainActivity implements SensorEventListener {

    private TextView textview;
    private SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    private Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    private Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textview = (TextView)findViewById(R.id.textView);
    }


    public void onSensorChanged(SensorEvent event){
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            textview.setText("Step Counter Detected : " + value);
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // For test only. Only allowed value is 1.0 i.e. for step taken
            textview.setText("Step Detector Detected : " + value);
        }
    }

    public void onAccuracyChanged(Sensor s, int i){

    }

    protected void onResume() {

        super.onResume();

        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, stepCounterSensor);
        sensorManager.unregisterListener(this, stepDetectorSensor);
    }
}
