package com.lykos.shakedemowithgraph;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity {

    TextView txt_current, txt_prevAccel,txt_accel;
    ProgressBar prog_ShakeMeter;
     //define sensor variables
    private  SensorManager mSensorManager;
    private  Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;
    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;
    private Viewport viewport;


    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });

    private SensorEventListener sensorEventListener = new SensorEventListener() {



        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            accelerationCurrentValue = Math.sqrt((x * x + y * y + z * z ));
            double ChangeInAccel = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;

            txt_current.setText("Current = " +(int) accelerationCurrentValue);
            txt_prevAccel.setText(("Prev = " + (int)accelerationPreviousValue));
            txt_accel.setText(("Acceleration change = " + (int)ChangeInAccel));


            prog_ShakeMeter.setProgress((int) ChangeInAccel);

            if (ChangeInAccel > 12){
                txt_accel.setBackgroundColor(Color.RED);
            }
            else if(ChangeInAccel > 5){
                txt_accel.setBackgroundColor(Color.parseColor("#fcad03"));
            }
            else if(ChangeInAccel > 2){
                txt_accel.setBackgroundColor(Color.DKGRAY);
            }else{
                txt_accel.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary_dark));
            }
            //Update the graph
            pointsPlotted++;
            if (pointsPlotted >500){
                pointsPlotted = 1;
                series.resetData(new DataPoint[]{new DataPoint(1,0)});
            }
            series.appendData(new DataPoint(pointsPlotted,ChangeInAccel),true,pointsPlotted);
            viewport.setMaxX(pointsPlotted);
            viewport.setMinX(pointsPlotted -200);



        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_accel = findViewById(R.id.txt_accel);
        txt_current = findViewById(R.id.txt_currentaccel);
        txt_prevAccel = findViewById(R.id.txt_prevAccelerat);
        prog_ShakeMeter = findViewById(R.id.progressBar);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(series);
        viewport = graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);


        //Initialize sensor variables
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);









    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

}