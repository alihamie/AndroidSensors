package com.dingohub.alerty.sensormanagement;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class SensorListener implements SensorEventListener {


    private static final String TAG = "SensorListener";

    private static final float ALPHA = 0.8f;
    private float[] gravity;
    private int highPassCount;

    private SimpleXYSeries xAxisSeries;
    private SimpleXYSeries yAxisSeries;
    private SimpleXYSeries zAxisSeries;
    private SimpleXYSeries accelerationSeries;
    private XYPlot xyPlot;
    private PrintWriter printWriter;
    private static final String CSV_HEADER =
            "X Axis,Y Axis,Z Axis,Acceleration,Time";
    private static final char CSV_DELIM = ',';
    private long startTime;
    private boolean useHighPassFilter;
    private long lastChartRefresh;

    public SensorListener(XYPlot xyPlot, File data, boolean useHighPassFilter) {
        this.xyPlot = xyPlot;
        this.useHighPassFilter = useHighPassFilter;
        xAxisSeries = new SimpleXYSeries("X Axis");
        yAxisSeries = new SimpleXYSeries("Y Axis");
        zAxisSeries = new SimpleXYSeries("Z Axis");
        accelerationSeries = new SimpleXYSeries("Acceleration");

        gravity = new float[3];
        highPassCount = 0;

        startTime = SystemClock.uptimeMillis();
        try {
            printWriter =
                    new PrintWriter(new BufferedWriter(new FileWriter(data)));

            printWriter.println(CSV_HEADER);
        } catch (IOException e) {
            Log.e(TAG, "Could not open the file", e);
        }


        if (xyPlot != null) {
            xyPlot.addSeries(xAxisSeries,
                    new LineAndPointFormatter(Color.RED, null, null, (PointLabelFormatter) null));
            xyPlot.addSeries(yAxisSeries,
                    new LineAndPointFormatter(Color.GREEN, null, null, (PointLabelFormatter) null));
            xyPlot.addSeries(zAxisSeries,
                    new LineAndPointFormatter(Color.BLUE, null, null, (PointLabelFormatter) null));
            xyPlot.addSeries(accelerationSeries,
                    new LineAndPointFormatter(Color.CYAN, null, null, (PointLabelFormatter) null));
        }


    }


    private void writeSensorEvent(PrintWriter printWriter,
                                  float x,
                                  float y,
                                  float z,
                                  double acceleration,
                                  long eventTime) {
        if (printWriter != null) {
            StringBuffer sb = new StringBuffer()
                    .append(x).append(CSV_DELIM)
                    .append(y).append(CSV_DELIM)
                    .append(z).append(CSV_DELIM)
                    .append(acceleration).append(CSV_DELIM)
                    .append((eventTime / 1000000) - startTime);

            printWriter.println(sb.toString());
            if (printWriter.checkError()) {
                Log.w(TAG, "Error writing sensor event data");
            }
        }
    }


    private float[] highPass(float x, float y, float z) {
        float[] filteredValues = new float[3];

        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

        filteredValues[0] = x - gravity[0];
        filteredValues[1] = y - gravity[1];
        filteredValues[2] = z - gravity[2];

        return filteredValues;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values.clone();

        // Pass values through high-pass filter if enabled
        if (useHighPassFilter) {
            values = highPass(values[0],
                    values[1],
                    values[2]);
        }


        // Ignore data if the high-pass filter is enabled, has not yet received
        // some data to set it
        if (!useHighPassFilter || (++highPassCount >= 10)) {
            double sumOfSquares = (values[0] * values[0])
                    + (values[1] * values[1])
                    + (values[2] * values[2]);
            double acceleration = Math.sqrt(sumOfSquares);

            // Write to data file
            writeSensorEvent(printWriter,
                    values[0],
                    values[1],
                    values[2],
                    acceleration,
                    event.timestamp);

            // If the plot is null, the sensor is not active. Do not plot the
            // data or used the data to determine if the device is moving
            if (xyPlot != null) {
                long current = SystemClock.uptimeMillis();

                // Limit how much the chart gets updated
                if ((current - lastChartRefresh) >= 125) {
                    long timestamp = (event.timestamp / 1000000) - startTime;

                    // Plot data
                    addDataPoint(xAxisSeries, timestamp, values[0]);
                    addDataPoint(yAxisSeries, timestamp, values[1]);
                    addDataPoint(zAxisSeries, timestamp, values[2]);
                    addDataPoint(accelerationSeries, timestamp, acceleration);

                    xyPlot.redraw();

                    lastChartRefresh = current;
                }


            }
        }

    }

    private void addDataPoint(SimpleXYSeries series,
                              Number timestamp,
                              Number value) {
        if (series.size() == 30) {
            series.removeFirst();
        }

        series.addLast(timestamp, value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void stop()
    {

        if (printWriter != null)
        {
            printWriter.close();
        }

        if (printWriter.checkError())
        {
            Log.e(TAG, "Error closing writer");
        }
    }
}
