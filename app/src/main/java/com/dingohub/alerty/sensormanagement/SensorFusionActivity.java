package com.dingohub.alerty.sensormanagement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;


public class SensorFusionActivity extends ActionBarActivity implements SensorEventListener {



    SensorManager sensorManager;
    float[] gData = new float[3];           // Gravity or accelerometer
    float[] mData = new float[3];           // Magnetometer
    float[] orientation = new float[3];
    float[] Rmat = new float[9];
    float[] R2 = new float[9];
    float[] Imat = new float[9];
    boolean haveGrav = false;
    boolean haveAccel = false;
    boolean haveMag = false;
    private double DEG = 180.0 / Math.PI;
    private  final String TAG = "SensorFusion";
    private TextView xRotation_text;
    private TextView yRotation_text;
    private TextView inclination_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_fusion);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sensorManager =
                (SensorManager)getSystemService(getApplicationContext().SENSOR_SERVICE);;

        xRotation_text = (TextView)findViewById(R.id.pitch_text);
        yRotation_text =(TextView)findViewById(R.id.roll_text);
        inclination_text = ( TextView)findViewById(R.id.inclination_text);
    }

    @Override
    public void onResume()
    {super.onResume();

        Sensor gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, asensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor_fusion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] data;
        switch( event.sensor.getType() ) {
            case Sensor.TYPE_GRAVITY:
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                haveGrav = true;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                if (haveGrav) break;    // don't need it, we have better
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                haveAccel = true;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mData[0] = event.values[0];
                mData[1] = event.values[1];
                mData[2] = event.values[2];
                haveMag = true;
                break;
            default:
                return;
        }
        if ((haveGrav || haveAccel) && haveMag) {
            SensorManager.getRotationMatrix(Rmat, Imat, gData, mData);
            SensorManager.remapCoordinateSystem(Rmat,
                    SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R2);



            SensorManager.getOrientation(R2, orientation);
            float incl = SensorManager.getInclination(Imat);


            //pitch is the rotation in the x axis
            //roll is the rotation on the y-axis
            //inclination is the inclination of th  e magnetic field
            xRotation_text.setText("X-axis rotation" + (int)(orientation[1]*DEG) );
            yRotation_text.setText("Y-axis rotation"+ (int)(orientation[2]*DEG));
            inclination_text.setText("inclination: "+(int) (incl *DEG));
            Log.d(TAG, "pitch: " + (int)(orientation[1]*DEG));
           //  Log.d(TAG, "roll: " + (int)(orientation[2]*DEG));
            // Log.d(TAG, "inclination: " + (int) (incl * DEG));

        }

    }


    @Override
    public void onPause()
    {super.onPause();
        sensorManager.unregisterListener(this);

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
