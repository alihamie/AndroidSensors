package com.dingohub.alerty.sensormanagement;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Main_Screen extends ActionBarActivity {


    private Button graph_activity_button;
    private Button lightSensor_activity_button;
    private Button sensorFusion_button;
    private Intent activity_intent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__screen);

        graph_activity_button = (Button)findViewById(R.id.sensor_graph_button);
        toolbar = (Toolbar)findViewById(R.id.material_toolbar);
        lightSensor_activity_button = (Button)findViewById(R.id.sensor_light_button);
        sensorFusion_button = (Button)findViewById(R.id.sensor_fusion_button);
        setSupportActionBar(toolbar);

        sensorFusion_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_intent = new Intent(getApplicationContext(),SensorFusionActivity.class);
                startActivity(activity_intent);
            }
        });

        lightSensor_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_intent = new Intent(getApplicationContext(),LightSensorActivity.class);
                startActivity(activity_intent);
            }
        });

        graph_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(activity_intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main__screen, menu);
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
}
