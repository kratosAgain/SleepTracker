package com.gouravapp.janacare;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener  {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private TextView text1;
    private TextView showData;
    private Button sleepButton;
    private Button wakeButton;
    private long startTime;
    private long endTime;
    private int id ;
    private boolean sleeping = false;
    Calendar real_time;

    DatabaseHandler db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = (TextView) findViewById(R.id.text1);
        showData = (TextView)findViewById(R.id.showData);
        sleepButton = (Button) findViewById(R.id.sleepButton);
        wakeButton = (Button) findViewById(R.id.wakeButton);
        db = new DatabaseHandler(this);
    }




    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        Toast toast;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER && this.sleeping==true) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) >= 1) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
                int speed_int = (int)speed;
                if (speed_int > 1) {

                    //String text = "x: "+x+" y:"+y+" z:"+z;
                    //toast = Toast.makeText(this,text,Toast.LENGTH_LONG);
                    //text1.setText(text);
                    //toast.show();

                    Log.d("Insert: ", "Inserting ..");
                    this.db.addTime(new TimeEntry(this.id++,curTime-startTime,speed_int));

                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void onSleep(View view){
//        if(db.getCount()>1){
//            db.drop();
//        }

        db.delete();
        real_time = Calendar.getInstance();
        showData.setText("TRACKING YOUR SLEEP!! \nPRESS 'WOKE UP' when you are done");
        startTime = System.currentTimeMillis();
        this.sleeping = true;
        this.id = 0;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onWake(View view){
        if(this.sleeping==false) {
            showData.setText(" You are already awake");
            return;
        }
        this.endTime = System.currentTimeMillis()-this.startTime;
        this.sleeping = false;
        mSensorManager.unregisterListener(this);
        List<TimeEntry> list = db.getAll();
//        String s = "";
//        for(TimeEntry t:list){
//            s = s + t.id+"  "+t.time+"  "+t.speed+"\n";
//        }

        AnalyzeSleep anal = new AnalyzeSleep(list,0,this.endTime-this.startTime,this.real_time,db);
        String result = anal.getResult();
        showData.setText(result);

    }



}


