package com.dingohub.alerty.sensormanagement;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;

import java.io.File;


public class MainActivity extends ActionBarActivity {


    private RadioGroup sensorSelector;
    private CheckBox highPassFilterCheckBox;
    private SensorManager sensorManager;
    private XYPlot xyPlot;
    private boolean Plotting;
    private int selectedSensorType;

    private SensorListener accelerometerListener;
    private SensorListener linearAccelerationListener;
    private boolean useHighPassFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sensorSelector = (RadioGroup)findViewById(R.id.sensorSelector);
        highPassFilterCheckBox = (CheckBox) findViewById(R.id.highPassFilterCheckBox);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        ((RadioButton)findViewById(R.id.linearAcceleration)).setChecked(true);
        ((RadioButton)findViewById(R.id.accelerometer)).setChecked(false);
        selectedSensorType = Sensor.TYPE_ACCELEROMETER;
        Plotting = false;

        xyPlot = (XYPlot)findViewById(R.id.XYPlot);
        xyPlot.setDomainLabel("Elapsed Time (ms)");
        xyPlot.setRangeLabel("Acceleration (m/sec^2)");
        xyPlot.setBorderPaint(null);
        xyPlot.setRangeBoundaries(-30,30, BoundaryMode.FIXED);

    }



    private void startPlottingData()
    {
        if(!Plotting)
        {

            xyPlot.clear();
            xyPlot.redraw();

            for (int i = 0; i < sensorSelector.getChildCount(); i++)
            {
                sensorSelector.getChildAt(i).setEnabled(false);
            }

            highPassFilterCheckBox.setEnabled(false);

            File accFile = new File(getExternalCacheDir(), "accelerometer.csv");
            File linearFile =new File(getExternalCacheDir(),"linear.csv");


            if (selectedSensorType == Sensor.TYPE_ACCELEROMETER)
            {
                xyPlot.setTitle("Sensor.TYPE_ACCELEROMETER");
                accelerometerListener =
                        new SensorListener(xyPlot,
                                accFile,
                               useHighPassFilter);

                linearAccelerationListener =
                        new SensorListener(null,
                                linearFile,
                               useHighPassFilter
                            );
            }
            else
            {
                xyPlot.setTitle("Sensor.TYPE_LINEAR_ACCELERATION");
                accelerometerListener =
                        new SensorListener(null,
                                accFile,
                                useHighPassFilter
                               );

                linearAccelerationListener =
                        new SensorListener(xyPlot,
                                linearFile,
                                useHighPassFilter
                               );
            }

            sensorManager.registerListener(accelerometerListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);

            sensorManager.registerListener(linearAccelerationListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                    SensorManager.SENSOR_DELAY_NORMAL);

            Plotting = true;
        }

    }


    private void stopReadingAccelerationData()
    {
        if (Plotting)
        {
            // Re-enable sensor and options UI views
            for (int i = 0; i < sensorSelector.getChildCount(); i++)
            {
                sensorSelector.getChildAt(i).setEnabled(true);
            }

            highPassFilterCheckBox.setEnabled(true);

            sensorManager.unregisterListener(accelerometerListener);
            sensorManager.unregisterListener(linearAccelerationListener);

            // Tell listeners to clean up after themselves
            accelerometerListener.stop();
            linearAccelerationListener.stop();

            Plotting = false;


        }
    }


    public void onHighPassFilterCheckBoxClicked(View view)
    {
        useHighPassFilter = ((CheckBox)view).isChecked();

    }

    public void onReadAccelerationDataToggleButtonClicked(View view)
    {
        ToggleButton toggleButton = (ToggleButton)view;

        if (toggleButton.isChecked())
        {
            startPlottingData();
        }
        else
        {
            stopReadingAccelerationData();
        }
    }






    public void onSensorSelectorClick(View view)
    {
        int selectedSensorId = sensorSelector.getCheckedRadioButtonId();
        if (selectedSensorId == R.id.accelerometer)
        {
            selectedSensorType = Sensor.TYPE_ACCELEROMETER;
        }
        else if (selectedSensorId == R.id.linearAcceleration)
        {
            selectedSensorType = Sensor.TYPE_LINEAR_ACCELERATION;
        }


    }


    @Override
    protected void onPause()
    {
        super.onPause();
        stopReadingAccelerationData();


    }


}
