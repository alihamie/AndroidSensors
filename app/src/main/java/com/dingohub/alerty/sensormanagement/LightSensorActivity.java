package com.dingohub.alerty.sensormanagement;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class LightSensorActivity extends ActionBarActivity {


    private SensorManager sensorManager;
    private int sensorType;
    private  Sensor lightSensor;
    private TextView sensor_value;
    private  SensorEventListener lightSensorListener;
    private AudioManager audioManager;
    private  int volumeTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sensor);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensor_value = (TextView) findViewById(R.id.sensor_text);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeTracker = 0;
        lightSensorListener = new SensorEventListener() {
           @Override
           public void onSensorChanged(SensorEvent event) {
               float values [] = event.values.clone();
               Log.d("LightSensor","" +values[0]);

               if(values[0]  <= 2 && volumeTracker < 15)
               {
                   volumeTracker ++;
                   audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
               }

               if(volumeTracker >= 15)
               {
                   sensor_value.setText("Volume is now Max!");
               }
           }

           @Override
           public void onAccuracyChanged(Sensor sensor, int accuracy) {

           }
       };

        sensorManager.registerListener(lightSensorListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);

    }





    public  void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);

    }
}
