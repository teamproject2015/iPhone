package de.unimannheim.touchlogger;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Random;


public class KeyboardActivity extends Activity implements
        SensorEventListener {

    private static final String FILENAME = "KeyboardTouchLogger";
    private static final String CLASS_NAME = KeyboardActivity.class.getName();
    private static String ALPHANUMERIC_RANDOMLETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final int KEYSTROKE_COUNT = 10;

    private int count;

    private String csvFileName;

    private StringBuffer logValues = new StringBuffer();
    private String generatedKey;
    private static Random rnd = new Random();
    private char[] randomKeys;
    private int charCount;
    private String pressedKey;
    private int targetNumber = 0;
    private float coordinateX, coordinateY;

    private SensorManager mSensorManager;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    String username;
    String layout;
    String orientation_device;
    String variation;
    String hardwareAddon;
    String input;
    String posture;
    String externalFactors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchlogger_keyboard);
        generateRandomKey(ALPHANUMERIC_RANDOMLETTERS);
        pressedKey = "";

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        int orientation = bundle.getInt("orientationPosition");


        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/TouchLogger");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        //String[] files = directory.list();
        int count = 0;
        if(directory.list()!=null){
            for (String file : directory.list()) {
                if (file.contains(FILENAME)) {
                    count++;
                }
            }
        }
        csvFileName = FILENAME + count + ".csv";


        Log.d(CLASS_NAME, "orientation-->" + orientation);
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerSensor();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        Bundle mybundle = getIntent().getExtras();
        username = mybundle.getString("username");
        layout = mybundle.getString("layout");
        orientation_device = String.valueOf(mybundle.getInt("orientation"));
        variation = mybundle.getString("variation");
        hardwareAddon = mybundle.getString("hardwareaddons");
        input = mybundle.getString("input");
        posture = mybundle.getString("posture");
        externalFactors = mybundle.getString("externalfactors");



    }


    private void registerSensor() {
        List<Sensor> mList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        //Creating List view and writing data to a file
        for (int i = 0; i < mList.size(); i++) {
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(mList.get(i).getType()), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float[] values = event.values;

        // Enable feeding data from 6 sensors
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            populateSensorData("Acceleration", values[0], values[1], values[2]);
        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            populateSensorData("Gyroscope", values[0], values[1], values[2]);
        } else if (sensorType == Sensor.TYPE_GRAVITY) {
            populateSensorData("Gravity", values[0], values[1], values[2]);
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            populateSensorData("Magnitude", values[0], values[1], values[2]);
        } else if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION) {
            populateSensorData("Linear Acceleration", values[0], values[1], values[2]);
        } else if (sensorType == Sensor.TYPE_ROTATION_VECTOR) {
            populateSensorData("Rotation Vector", values[0], values[1], values[2]);
        } else if (sensorType == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            populateSensorData("Geomagnetic Rotation Vector", values[0], values[1], values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class SaveCSVFile extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            return writeToFile(getBundleData(), params[0]);
        }

        @Override
        protected void onPostExecute(Boolean isFinished) {
            if (isFinished) {
                Toast.makeText(getApplicationContext(),
                        "Key Stocks Saved",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Key Stocks not saved",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    SaveCSVFile saveCSVFile = new SaveCSVFile();
                    saveCSVFile.execute(logValues.toString());
                    logValues.setLength(0);
                    count = 0;

                } else {
                    checkPermission();
                }
                break;
            }
        }
    }


    /**
     * Writing the Data to File to ExternalStorageDirectory
     *
     * @param data data which contains the Logger values
     */
    protected boolean writeToFile(String userDetails, String data) {
        //Log.i(CLASS_NAME,"data--------->"+data);
        boolean isNewFile = false;
        boolean isFinished = false;
        try {

            // Here, thisActivity is the current activity
            File sdCard = Environment.getExternalStorageDirectory();

            Log.i(CLASS_NAME, "sdCard-->" + sdCard.getAbsolutePath());
            File directory = new File(sdCard.getAbsolutePath() + "/TouchLogger");

            if (!directory.exists()) {
                if(directory.mkdirs()){
                    Log.i(CLASS_NAME, "Directory created Successfully " );
                } else {
                    Log.i(CLASS_NAME, "Directory creation failed " );
                }
            }


            File file = new File(directory, csvFileName);
            FileOutputStream fos;
            if (!file.exists()) {
                isNewFile = true;
            }
            try {
                fos = new FileOutputStream(file, true);
                Log.d(CLASS_NAME, "Data is append to the File " + csvFileName);
            } catch (FileNotFoundException fileNotFou0ndException) {
                fos = new FileOutputStream(file);
                Log.d(CLASS_NAME, "File " + csvFileName + " is created with new data");
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);

            if (isNewFile) {
                outputStreamWriter.write(userDetails + getString(R.string.file_headerfields) + "\r\n" + data);
            } else {
                outputStreamWriter.write(data);
            }

            outputStreamWriter.close();
            isFinished = true;

        } catch (IOException e) {
            Log.e(CLASS_NAME, "File write failed: " + e.toString());
        }

        GenerateFeaturesFile generate = new GenerateFeaturesFile();
        generate.writeCSVFeaturesFile(csvFileName);

        return isFinished;



    }



    private void checkPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            /*RelativeLayout layout= (RelativeLayout) getLayoutInflater().inflate(R.layout.common_touchlogger, null);
            Snackbar.make(layout, "premissionsdf so an so ", Snackbar.LENGTH_LONG).show();*/
            //checkPermission();
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    /**
     * When the User press/ touch the screen the event from OnTouchevent will be
     * triggering the onTouch method, the method will save the x,y coordinates
     * and accelerometer and orientation coordinates
     *
     * @param event keyevent
     * @return boolean value
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);

       /* if ("".equals(keyValue.getText().toString())) {
            return true;
        }*/

        //Log.i("Record", "Key=" + keyValue.getText().toString());
        //
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int pointerIndex = event.getActionIndex();

            //Log.i("TOUCHDOWN", "Key=" + keyValue.getText().toString()
            // + ",coordinateX=" + event.getX(pointerIndex) + ",coordinateY=" + event.getY(pointerIndex));
            recordTouchEvent(event.getX(pointerIndex), event.getY(pointerIndex), generatedKey);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {

            //case MotionEvent.ACTION_UP
            //this is the time in milliseconds
            long upTime = System.nanoTime();

            recordTouchEvent(0, 0, "");

            count++;
            if (count == KEYSTROKE_COUNT) {
                    /*writeToFile(logValues.toString(), FILENAME);
                    Toast.makeText(getApplicationContext(),
                            "Key Stocks Saved",
                            Toast.LENGTH_SHORT).show();*/

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        checkPermission();

                    } else {
                        SaveCSVFile saveCSVFile = new SaveCSVFile();
                        saveCSVFile.execute(logValues.toString());
                        logValues.setLength(0);
                        count = 0;
                    }
                } else {
                    SaveCSVFile saveCSVFile = new SaveCSVFile();
                    saveCSVFile.execute(logValues.toString());
                    logValues.setLength(0);
                    count = 0;
                }
            }

            generateRandomKey(ALPHANUMERIC_RANDOMLETTERS);

            /*//final EditText textMessage = (EditText) findViewById(R.id.editText_key);
            //Log.i(CLASS_NAME, "generatedKey value = " + generatedKey);
            TextWatcher tw = new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    generateRandomKey(ALPHANUMERIC_RANDOMLETTERS);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            };
            keyValue.addTextChangedListener(tw);
            keyValue.setText("");
            keyValue.removeTextChangedListener(tw);*/

        }

        return true;
    }

    protected void generateRandomKey(String randomLetters) {

        if (randomKeys == null || randomKeys.length == 0) {
            randomLetters = new String(new char[40]).replace("\0", randomLetters);
            //String value = String.valueOf(randomLetters.charAt(rnd.nextInt(randomLetters.length())));
            randomKeys = shuffleArray(randomLetters.toCharArray());
            //randomKeys = rnd.nextInt(26) + (byte)'a';
            //randomKeys = rnd.nextInt(10);
            charCount = 0;
        }

        generatedKey = String.valueOf(randomKeys[charCount++]);


        if (generatedKey.equals("a")){targetNumber = 1;}
        else if (generatedKey.equals("b")){targetNumber = 2;}
        else if (generatedKey.equals("c")){targetNumber = 3;}
        else if (generatedKey.equals("d")){targetNumber = 4;}
        else if (generatedKey.equals("e")){targetNumber = 5;}
        else if (generatedKey.equals("f")){targetNumber = 6;}
        else if (generatedKey.equals("g")){targetNumber = 7;}
        else if (generatedKey.equals("h")){targetNumber = 8;}
        else if (generatedKey.equals("i")){targetNumber = 9;}
        else if (generatedKey.equals("j")){targetNumber = 10;}
        else if (generatedKey.equals("k")){targetNumber = 11;}
        else if (generatedKey.equals("l")){targetNumber = 12;}
        else if (generatedKey.equals("m")){targetNumber = 13;}
        else if (generatedKey.equals("n")){targetNumber = 14;}
        else if (generatedKey.equals("o")){targetNumber = 15;}
        else if (generatedKey.equals("p")){targetNumber = 16;}
        else if (generatedKey.equals("q")){targetNumber = 17;}
        else if (generatedKey.equals("r")){targetNumber = 18;}
        else if (generatedKey.equals("s")){targetNumber = 19;}
        else if (generatedKey.equals("t")){targetNumber = 20;}
        else if (generatedKey.equals("u")){targetNumber = 21;}
        else if (generatedKey.equals("v")){targetNumber = 22;}
        else if (generatedKey.equals("w")){targetNumber = 23;}
        else if (generatedKey.equals("x")){targetNumber = 24;}
        else if (generatedKey.equals("y")){targetNumber = 25;}
        else if (generatedKey.equals("z")){targetNumber = 26;}
        else if (generatedKey.equals("1")){targetNumber = 27;}
        else if (generatedKey.equals("2")){targetNumber = 28;}
        else if (generatedKey.equals("3")){targetNumber = 29;}
        else if (generatedKey.equals("4")){targetNumber = 30;}
        else if (generatedKey.equals("5")){targetNumber = 31;}
        else if (generatedKey.equals("6")){targetNumber = 32;}
        else if (generatedKey.equals("7")){targetNumber = 33;}
        else if (generatedKey.equals("8")){targetNumber = 34;}
        else if (generatedKey.equals("9")){targetNumber = 35;}
        else if (generatedKey.equals("0")){targetNumber = 36;}
        else {targetNumber=0;}


        //String miau = String.valueOf(targetNumber);
        //Toast.makeText(this, generatedKey + miau, Toast.LENGTH_SHORT).show();



        if (randomKeys.length - 1 == charCount) {
            generatedKey = "All keys are finished";
        }

        TextView generateKeyForTextView = (TextView) findViewById(R.id.textView_key);
        generateKeyForTextView.setText(generatedKey);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    protected void recordTouchEvent(float x, float y, String key) {

        this.pressedKey = key;
        this.coordinateX = x;
        this.coordinateY = y;
        // Log.i("Record", "Key=" + this.pressedKey + ",coordinateX=" + this.coordinateX +",coordinateY=" + this.coordinateY);
    }

    protected String getBundleData() {
        Bundle bundle = getIntent().getExtras();
        StringBuilder temp = new StringBuilder();

        temp.append("User: ").append(getBundle(bundle, "username")).append("\r\n");
        temp.append("Layout: ").append(getBundle(bundle, "layout")).append("\r\n");
        temp.append("Orientation: ").append(getBundle(bundle, "orientation")).append("\r\n");
        temp.append("Variation: ").append(getBundle(bundle, "variation")).append("\r\n");
        temp.append("Hardwareaddons: ").append(getBundle(bundle, "hardwareaddons")).append("\r\n");
        temp.append("Input: ").append(getBundle(bundle, "input")).append("\r\n");
        temp.append("Posture: ").append(getBundle(bundle, "posture")).append("\r\n");
        temp.append("Externalfactors: ").append(getBundle(bundle, "externalfactors")).append("\r\n").append("\r\n");
        //data = temp.toString();
        return temp.toString();
    }


    private void populateSensorData(String name, float x, float y, float z) {
        //Log.i(CLASS_NAME,sensorData.toString());
        //Appending the key and Co-ordinates
        logValues.append(name).append(";").append(System.nanoTime()).append(";").append(pressedKey).append(";")
                .append(coordinateX).append(";").append(coordinateY).append(";").append(x).append(";")
                .append(y).append(";").append(z).append(";").append(targetNumber).append(";")
                .append(username).append(";").append(layout).append(";").append(orientation_device).append(";")
                .append(variation).append(";").append(hardwareAddon).append(";").append(input).append(";")
                .append(posture).append(";").append(externalFactors).append("\r\n");


    }

    /**
     * c This method returns the Bundle value for the @param value
     *
     * @param bundle Bundle Object Should be passed
     * @param value  Key for the Bundle
     * @return string
     */
    protected String getBundle(Bundle bundle, String value) {
        return bundle.getString(value);
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


    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        registerSensor();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ALL), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
