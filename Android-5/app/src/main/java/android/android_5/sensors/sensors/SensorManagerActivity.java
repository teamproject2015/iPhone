package android.android_5.sensors.sensors;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.android_5.BaseActivity;
import android.android_5.R;
import android.android_5.session.SessionManager;
import android.android_5.utils.Constants;

/**
 * Adding all the sensor data from this file
 */
public class SensorManagerActivity extends BaseActivity {

    private static final String CLASS_NAME = "SensorManagerActivity";

    private final float[] deltaRotationVector = new float[4];
    protected SensorManager mSensorManager;
    protected Sensor mAccelerometer;
    protected Sensor mTemperature;
    protected Sensor mPressure;
    protected Sensor mLight;
    protected Sensor mMagneticField;
    protected Sensor mGravity;
    protected Sensor mGyroscope;
    protected float lux;
    protected float hPa;
    protected StringBuffer logValues = new StringBuffer();
    protected String generatedKey;
    protected String data;
    static Random rnd = new Random();
    private char[] randomKeys;
    private int charCount;

    protected ArrayList<SensorData> accelerometerData = new ArrayList<SensorData>();
    protected ArrayList<SensorData> gravityData = new ArrayList<SensorData>();
    protected ArrayList<SensorData> magnitudeData = new ArrayList<SensorData>();
    protected ArrayList<SensorData> gyroscopeData = new ArrayList<SensorData>();
    protected ArrayList<SensorData> orientationData = new ArrayList<SensorData>();

    protected AccelerometerResultReceiver accelerometerResultReceiver;
    protected OrientationResultReceiver orientationResultReceiver;
    protected GyroscopeResultReceiver gyroscopeResultReceiver;
    protected GravityResultReceiver gravityResultReceiver;
    protected PressureResultReceiver pressureResultReceiver;
    protected LightResultReceiver lightResultReceiver;
    protected MagnitudeResultReceiver magnitudeResultReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerSensors();
        startServices();
    }

    public void startServices() {

        // start accelerator service
        if (accelerometerResultReceiver != null) {
            Intent accIntent = new Intent(this, AccelerometerService.class);
            accIntent.putExtra(Constants.EXTRA_RECEIVER,accelerometerResultReceiver);
            startService(accIntent);
        }

        // start orientation service
        if (orientationResultReceiver != null) {
            Intent orientationIntent = new Intent(this,OrientationService.class);
            orientationIntent.putExtra(Constants.EXTRA_RECEIVER,orientationResultReceiver);
            startService(orientationIntent);
        }

        // start gyroscope service
        if (gyroscopeResultReceiver != null) {
            Intent gyroscopeIntent = new Intent(this, GyroscopeService.class);
            gyroscopeIntent.putExtra(Constants.EXTRA_RECEIVER, gyroscopeResultReceiver);
            startService(gyroscopeIntent);
        }

        // start gravity service
        if (gravityResultReceiver != null) {
            Intent gravityIntent = new Intent(this, GravityService.class);
            gravityIntent.putExtra(Constants.EXTRA_RECEIVER, gravityResultReceiver);
            startService(gravityIntent);
        }

        // start pressure service
        if (pressureResultReceiver != null) {
            Intent pressureIntent = new Intent(this, PressureService.class);
            pressureIntent.putExtra(Constants.EXTRA_RECEIVER, pressureResultReceiver);
            startService(pressureIntent);
        }

        // start light service
        if (lightResultReceiver != null) {
            Intent lightIntent = new Intent(this, LightService.class);
            lightIntent.putExtra(Constants.EXTRA_RECEIVER, lightResultReceiver);
            startService(lightIntent);
        }

        // start light service
        if (magnitudeResultReceiver != null) {
            Intent magnitudeIntent = new Intent(this, MagnitudeService.class);
            magnitudeIntent.putExtra(Constants.EXTRA_RECEIVER, magnitudeResultReceiver);
            startService(magnitudeIntent);
        }


    }

    /**
     * Registering all the required Sensors to the App
     */
    protected void registerSensors() {

        Handler handler = new Handler();

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sm.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i < sensorList.size(); i++) {
            int type = sensorList.get(i).getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                accelerometerResultReceiver = new AccelerometerResultReceiver(handler);
                accelerometerResultReceiver.setReceiver(new AccelerometerReceiver());

            } else if (type == Sensor.TYPE_GRAVITY) {
                gravityResultReceiver = new GravityResultReceiver(handler);
                gravityResultReceiver.setReceiver(new GravityReceiver());

            } else if (type == Sensor.TYPE_GYROSCOPE) {
                gyroscopeResultReceiver = new GyroscopeResultReceiver(handler);
                gyroscopeResultReceiver.setReceiver(new GyroscopeReceiver());

            } else if (type == Sensor.TYPE_PRESSURE) {
                pressureResultReceiver = new PressureResultReceiver(handler);
                pressureResultReceiver.setReceiver(new PressureReceiver());

            } else if (type == Sensor.TYPE_LIGHT) {
                lightResultReceiver = new LightResultReceiver(handler);
                lightResultReceiver.setReceiver(new LightReceiver());

            } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                magnitudeResultReceiver = new MagnitudeResultReceiver(handler);
                magnitudeResultReceiver.setReceiver(new MagnitudeReceiver());
            }
        }
        orientationResultReceiver = new OrientationResultReceiver(handler);
        orientationResultReceiver.setReceiver(new OrientationReceiver());
    }

    /**
     * This method generates the Key to press on the screen to monitor
     * the key stocks
     */
    protected void generateRandomKey(String randomLetters) {

        if (randomKeys == null || randomKeys.length == 0) {
            randomLetters = new String(new char[40]).replace("\0", randomLetters);
            randomKeys = shuffleArray(randomLetters.toCharArray());
            charCount = 0;
        }
        generatedKey = String.valueOf(randomKeys[charCount++]);
        if (randomKeys.length - 1 == charCount) {
            generatedKey = "All keys are finished";
        }

        TextView generateKeyForTextView = (TextView) findViewById(R.id.textView_key);
        generateKeyForTextView.setText(generatedKey);
    }

    /*
     * Implementing Fisher–Yates shuffle
     */
    static char[] shuffleArray(char[] ar) {
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            char a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
        return ar;
    }


    protected boolean recordTouchEvent(MotionEvent event, String key,
                                       long downTime, long upTime,
                                       Date beginningTimestamp, Date endingTimestamp) {

        StringBuilder keyStroke = new StringBuilder();


        //Appending the Data
        keyStroke.append(data);

        //Appending the key
        keyStroke.append(key).append(";");

        //multi touch pointers
        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get masked (not specific to a pointer) action
        //int maskedAction = event.getActionMasked();


        //Keystroke for Co-ordinates
        keyStroke.append(event.getX(pointerIndex)).append(";").append(event.getY(pointerIndex)).append(";");
        //Testing the Value of pointers by Log


        //Keystroke for taken Time/ms
        keyStroke.append(downTime).append(";");
        keyStroke.append(upTime).append(";");
        keyStroke.append(beginningTimestamp.toString()).append(";");
        keyStroke.append(endingTimestamp.toString()).append(";");
        StringBuilder tempBuffer = new StringBuilder();
        tempBuffer.append(keyStroke);

        //Only the first sensor value needs to be collected; so commented the below Code
        //Keystroke for gravity
        //keyStroke.append("Gravity").append(";").append(gravity.z).append(";").append(gravity.x).append(";").append(gravity.y).append("\r\n");
        populateSensorData("Gravity", gravityData, keyStroke, new StringBuilder(), downTime, upTime);
        //Keystroke for magnitude
        /*keyStroke.append(tempBuffer).append("Magnitude").append(";").append(magnitude[0]).append(";")
                .append(magnitude[1]).append(";").append(magnitude[2]).append("\r\n");*/
        populateSensorData("Magnitude", magnitudeData, keyStroke, tempBuffer, downTime, upTime);

        //Keystroke for Accelerometer
        //Log.i(CLASS_NAME, "accelerometerData-->" + accelerometerData.toString());
        populateSensorData("Acceleration", accelerometerData, keyStroke, tempBuffer, downTime, upTime);

        //Keystroke for orientation
        /*keyStroke.append(tempBuffer).append("Orientation").append(";").append(orientation[0]).append(";")
                .append(orientation[1]).append(";").append(orientation[2]).append("\r\n");*/
        populateSensorData("Orientation", orientationData, keyStroke, tempBuffer, downTime, upTime);

        /*keyStroke.append(tempBuffer).append("GyroScope").append(";").append(gyroscope[0]).append(";")
                .append(gyroscope[1]).append(";").append(gyroscope[2]).append("\r\n");*/
        populateSensorData("GyroScope", gyroscopeData, keyStroke, tempBuffer, downTime, upTime);


      /*  keyStroke.append(tempBuffer).append("RotationVector").append(",").append(rotationVector[0]).append(",")
                .append(rotationVector[1]).append(";").append(rotationVector[2]).append("\r\n");*/

        //Keystroke for Temperature
        //keyStroke.append(celsius).append(";");

        //Keystroke for Light
        keyStroke.append(tempBuffer).append("Light").append(";").append(lux).append(";").append(0).append(";").append(0).append("\r\n");
        //Keystroke for pressure
        keyStroke.append(tempBuffer).append("Pressure").append(";").append(hPa).append(";").append(0).append(";").append(0).append("\r\n");

        logValues.append(keyStroke);
        //Log.i(CLASS_NAME, "logValues------------->" + logValues);
        return true;
    }

    /**
     * Populate the SensorData to buffer
     *
     */
    public class RecordTouchEvent implements Runnable {
        private Thread t;
        private ArrayList<SensorData> sensorData;
        String name;
        StringBuilder keyStroke, tempBuffer;
        long downTime, upTime;

        public RecordTouchEvent(String name, ArrayList<SensorData> sensorData,
                                  StringBuilder keyStroke, StringBuilder tempBuffer, long downTime, long upTime) {
            this.sensorData = sensorData;
            this.keyStroke = keyStroke;
            this.tempBuffer = tempBuffer;
            this.downTime = downTime;
            this.upTime = upTime;
            this.name = name;
        }

        @Override
        public void run() {
            if (sensorData != null && sensorData.size() > 0) {
                StringBuilder xAxis = new StringBuilder();
                StringBuilder yAxis = new StringBuilder();
                StringBuilder zAxis = new StringBuilder();

                for (SensorData data : sensorData) {
                    //Log.i(CLASS_NAME, "data-->" + data.toString());
                    data.getTimestamp();
                    if (data.getTimestamp() >= downTime) {
                        if (xAxis.length() > 0) {
                            xAxis.append(",");
                            yAxis.append(",");
                            zAxis.append(",");
                        }
                        xAxis.append(data.getX());
                        yAxis.append(data.getY());
                        zAxis.append(data.getZ());
                        if (data.getTimestamp() >= upTime) {
                            break;
                        }
                    }
                }
                keyStroke.append(tempBuffer).append(name).append(";").append(xAxis.toString()).append(";")
                        .append(yAxis.toString()).append(";").append(zAxis.toString()).append("\r\n");
                sensorData.clear();
            } else {
                keyStroke.append(tempBuffer).append(name).append(";;;").append("\r\n");
            }
        }

        public void start() {
            Log.i(CLASS_NAME, "UpdateData Thread Started");
            if (t == null) {
                t = new Thread(this);
                t.start();
            }
        }
    }


    private void populateSensorData(String name, ArrayList<SensorData> sensorData,
                                    StringBuilder keyStroke, StringBuilder tempBuffer, long downTime, long upTime) {
        //Log.i(CLASS_NAME,sensorData.toString());
        if (sensorData.size() > 0) {
            //long t = accelerometerData.get(0).getTimestamp();
            StringBuilder xAxis = new StringBuilder();
            StringBuilder yAxis = new StringBuilder();
            StringBuilder zAxis = new StringBuilder();

            for (SensorData data : sensorData) {
                //Log.i(CLASS_NAME, "data-->" + data.toString());
                data.getTimestamp();
                if (data.getTimestamp() >= downTime) {
                    if (xAxis.length() > 0) {
                        xAxis.append(",");
                        yAxis.append(",");
                        zAxis.append(",");
                    }
                    xAxis.append(data.getX());
                    yAxis.append(data.getY());
                    zAxis.append(data.getZ());
                    if (data.getTimestamp() >= upTime) {
                        break;
                    }
                }
            }
            keyStroke.append(tempBuffer).append(name).append(";").append(xAxis.toString()).append(";")
                    .append(yAxis.toString()).append(";").append(zAxis.toString()).append("\r\n");
            sensorData.clear();
        } else {
            keyStroke.append(tempBuffer).append(name).append(";;;").append("\r\n");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }*/

    /*
    @Override
    public void onSensorChanged(SensorEvent event) {

        */

    /**
     * Value giving the total velocity of the gyroscope (will be high, when the device is moving fast and low when
     * the device is standing still). This is usually a value between 0 and 10 for normal motion. Heavy shaking can
     * increase it to about 25. Keep in mind, that these values are time-depended, so changing the sampling rate of
     * the sensor will affect this value!
     *//*
        int type = event.sensor.getType();
        event.sensor.getName();
        if (type == Sensor.TYPE_GRAVITY) {
            // Isolate the force of gravity with the low-pass filter.
            // gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            //gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            //gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            gravity.z = event.values[0];
            gravity.x = event.values[1];
            gravity.y = event.values[2];
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, event.values[0], event.values[1], event.values[2]);
            gravityData.add(data);

        } else if (type == Sensor.TYPE_ACCELEROMETER) {

            //acceleration[0] = event.values[0];
            //acceleration[1] = event.values[1];
            //acceleration[2] = event.values[2];
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, event.values[0], event.values[1], event.values[2]);
            accelerometerData.add(data);
            //Log.i(CLASS_NAME, "accelerometerData-->" + accelerometerData.toString());
            *//*gravity.z = averageList(acceleration[0]);
            gravity.x = averageList(acceleration[1]);
            gravity.y = averageList(acceleration[2]);*//*

        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {

            magnitude[0] = event.values[0];
            magnitude[1] = event.values[1];
            magnitude[2] = event.values[2];
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, event.values[0], event.values[1], event.values[2]);
            magnitudeData.add(data);

        } else if (type == Sensor.TYPE_ROTATION_VECTOR) {
            // This timestep's delta rotation to be multiplied by the current rotation
            // after computing it from the gyro sample data.
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                // (that is, EPSILON should represent your maximum allowable margin of error)
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;
            }
            timestamp = event.timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            // rotationCurrent = rotationCurrent * deltaRotationMatrix;


        } else if (type == Sensor.TYPE_GYROSCOPE) {

            //gyroscope = event.values.clone();
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, event.values[0], event.values[1], event.values[2]);
            gyroscopeData.add(data);


        } *//*else if (type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            celsius = event.values[0];
            sensorValues.append("Temperature").append(",").append(celsius).append(",");

        } *//* else if (type == Sensor.TYPE_PRESSURE) {
            hPa = event.values[0];
        } else if (type == Sensor.TYPE_LIGHT) {
            lux = event.values[0];

        } else {
            return;
        }

        if (gravity != null && magnitude != null) {
            SensorManager.getRotationMatrix(RTmp, I, gravity.get(), magnitude);

            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int rotation = display.getRotation();

            if (rotation == 1) {
                SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);
            } else {
                SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, Rot);
            }

            SensorManager.getOrientation(Rot, orientation);
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, orientation[0], orientation[1], orientation[2]);
            orientationData.add(data);
            *//*orientation[0] = (float) (((results[0] * 180) / Math.PI) + 180); //azimuth
            //Positive Roll is defined when the phone starts by laying flat
            // on a table and the positive Z-axis begins to tilt towards the positive X-axis.
            orientation[1] = (float) (((results[1] * 180 / Math.PI)) + 90); //pitch
            //Positive Pitch is defined when the phone starts by laying flat
            // on a table and the positive Z-axis begins to tilt towards the positive Y-axis.
            orientation[2] = (float) (((results[2] * 180 / Math.PI))); //roll*//*
        }
    }*/

  /*  public List<Float> roll(List<Float> list, float newMember){
        if(list.size() == MAX_SAMPLE_SIZE){
            list.remove(0);
        }
        list.add(newMember);
        return list;
    }*//*

    public float averageList(List<Float> tallyUp) {

        float total = 0;
        for (float item : tallyUp) {
            total += item;
        }
        total = total / tallyUp.size();

        return total;
    }*/
    protected void onResume() {
        super.onResume();
        startServices();
        /*mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);*/
    }

    protected void onPause() {
        super.onPause();
        accelerometerData.clear();
        gravityData.clear();
        magnitudeData.clear();
        gyroscopeData.clear();
        orientationData.clear();
        /*mSensorManager.unregisterListener(this);*/
        if (accelerometerResultReceiver != null)
            stopService(new Intent(this, AccelerometerService.class));
        if (orientationResultReceiver != null)
            stopService(new Intent(this, OrientationService.class));
        if (gyroscopeResultReceiver != null)
            stopService(new Intent(this, GyroscopeService.class));
        if (gravityResultReceiver != null)
            stopService(new Intent(this, GravityService.class));
        if (pressureResultReceiver != null)
            stopService(new Intent(this, PressureService.class));
        if (lightResultReceiver != null)
            stopService(new Intent(this, LightService.class));
        if (magnitudeResultReceiver != null)
            stopService(new Intent(this, MagnitudeService.class));
    }

    protected void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        StringBuilder temp = new StringBuilder();
        SessionManager session = new SessionManager(getApplicationContext());

        temp.append(session.getUserName()).append(";");
        temp.append(getBundle(bundle, "layout")).append(";");
        temp.append(getBundle(bundle, "orientation")).append(";");
        temp.append(getBundle(bundle, "variation")).append(";");
        temp.append(getBundle(bundle, "hardwareaddons")).append(";");
        temp.append(getBundle(bundle, "input")).append(";");
        temp.append(getBundle(bundle, "posture")).append(";");
        temp.append(getBundle(bundle, "externalfactors")).append(";");
        data = temp.toString();
    }


    private class AccelerometerReceiver implements
            AccelerometerResultReceiver.Receiver {

        public AccelerometerReceiver() {
        }

        @Override
        public void newEvent(float x, float y, float z) {
            //Log.i(CLASS_NAME,"x="+x+",y="+y+",z="+z);
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, x, y, z);
            accelerometerData.add(data);
            /*acceleration[0] = x;
            acceleration[1] = y;
            acceleration[2] = z;*/
           // writeToFile(x+","+y+","+z+"\r\n","Accelerometer.csv");
        }

        @Override
        public void error(String error) {
        }

    }

    private class GyroscopeReceiver implements GyroscopeResultReceiver.Receiver {

        public GyroscopeReceiver() {
        }

        @Override
        public void newEvent(float x, float y, float z) {
            //new UpdateData(gyroscopeData, x, y, z);
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, x, y, z);
            gyroscopeData.add(data);
           /* gyroscope[0] = x;
            gyroscope[1] = y;
            gyroscope[2] = z;*/
        }

        @Override
        public void error(String error) {
        }

    }

    private class GravityReceiver implements GravityResultReceiver.Receiver {

        public GravityReceiver() {
        }

        @Override
        public void newEvent(float x, float y, float z) {
            //new UpdateData(gravityData, x, y, z);
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, x, y, z);
            gravityData.add(data);
           /* gravity.x = x;
            gravity.y = y;
            gravity.z = z;*/
        }

        @Override
        public void error(String error) {
        }
    }

    private class PressureReceiver implements PressureResultReceiver.Receiver {

        public PressureReceiver() {
        }

        @Override
        public void newEvent(float x, float y, float z) {
            hPa = x;
        }

        @Override
        public void error(String error) {
        }
    }

    private class LightReceiver implements LightResultReceiver.Receiver {
        public LightReceiver() {
        }

        @Override
        public void newEvent(float x, float y, float z) {
            lux = x;
        }

        @Override
        public void error(String error) {
        }
    }

    private class MagnitudeReceiver implements MagnitudeResultReceiver.Receiver {

        public MagnitudeReceiver() {
        }

        @Override
        public void newEvent(float x, float y, float z) {
            //new UpdateData(magnitudeData, x, y, z);
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, x, y, z);
            magnitudeData.add(data);
           /* magnitude[0] = x;
            magnitude[1] = y;
            magnitude[2] = z;*/
        }

        @Override
        public void error(String error) {
        }
    }

    private class OrientationReceiver implements
            OrientationResultReceiver.Receiver {

        public OrientationReceiver() {
        }

        @Override
        public void newEvent(float x, float y, float z) {
            /*Log.i(CLASS_NAME,"entered Orientation");
            new UpdateData(orientationData, x, y, z);*/
            long timestamp = SystemClock.elapsedRealtime();
            SensorData data = new SensorData(timestamp, x, y, z);
            orientationData.add(data);
           /* orientation[0] = x;
            orientation[1] = y;
            orientation[2] = z;*/
        }

        @Override
        public void error(String error) {
        }

    }
}
