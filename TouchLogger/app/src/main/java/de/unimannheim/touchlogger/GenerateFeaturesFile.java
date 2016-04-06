/**
 *
 */
package de.unimannheim.touchlogger;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 */
public class GenerateFeaturesFile {


    private static final String CLASS_NAME = GenerateFeaturesFile.class.getName();
    private static final String HEADERS = "Session;Sensor;System_nanoTime;Key;target;Username;Layout;Orientation;Variation;Hardwareadds;Input;Posture;Externalfactors;KeyXCoordinate;KeyYCoordinate;timeDuration;"
            + "Sensor_XCoordinate_Mean;Sensor_XCoordinate_variance;Sensor_XCoordinate_stdDev;Sensor_XCoordinate_MinValue;Sensor_XCoordinate_MaxValue;"
            + "Sensor_YCoordinate_Mean;Sensor_YCoordinate_variance;Sensor_YCoordinate_stdDev;Sensor_YCoordinate_MinValue;Sensor_YCoordinate_MaxValue;"
            + "Sensor_ZCoordinate_Mean;Sensor_ZCoordinate_variance;Sensor_ZCoordinate_stdDev;Sensor_ZCoordinate_MinValue;Sensor_ZCoordinate_MaxValue";

    StringBuffer dataBuffer = new StringBuffer();
    String firstKey = "";
    String x = "", y = "";
    double x_mean, x_variance, x_minValue, x_maxValue;
    double y_mean, y_variance, y_minValue, y_maxValue;
    double z_mean, z_variance, z_minValue, z_maxValue;
    // double nanoTimeMean,nanoTimeVariance;
    long timeduration;
    double x_stdDev, y_stdDev, z_stdDev;
    File directory;
    String aux_string;

    String username;
    String layout;
    String orientation;
    String variation;
    String hardware_adds;
    String input;
    String posture;
    String external_factors;


    ArrayList<Double> x_gravityCoordinate = new ArrayList<>();
    ArrayList<Double> y_gravityCoordinate = new ArrayList<>();
    ArrayList<Double> z_gravityCoordinate = new ArrayList<>();
    ArrayList<Long> gravityNanoTime = new ArrayList<>();

    ArrayList<Double> x_gyroscopeCoordinate = new ArrayList<>();
    ArrayList<Double> y_gyroscopeCoordinate = new ArrayList<>();
    ArrayList<Double> z_gyroscopeCoordinate = new ArrayList<>();
    ArrayList<Long> gyroscopeNanoTime = new ArrayList<>();

    ArrayList<Double> x_magnitudeCoordinate = new ArrayList<>();
    ArrayList<Double> y_magnitudeCoordinate = new ArrayList<>();
    ArrayList<Double> z_magnitudeCoordinate = new ArrayList<>();
    ArrayList<Long> magnitudeNanoTime = new ArrayList<>();

    ArrayList<Double> x_accelerometerCoordinate = new ArrayList<>();
    ArrayList<Double> y_accelerometerCoordinate = new ArrayList<>();
    ArrayList<Double> z_accelerometerCoordinate = new ArrayList<>();
    ArrayList<Long> accelerometerNanoTime = new ArrayList<>();

    ArrayList<Double> x_linearaccelerationCoordinate = new ArrayList<>();
    ArrayList<Double> y_linearaccelerationCoordinate = new ArrayList<>();
    ArrayList<Double> z_linearaccelerationCoordinate = new ArrayList<>();
    ArrayList<Long> linearaccelerationNanoTime = new ArrayList<>();

    ArrayList<Double> x_rotationvectorCoordinate = new ArrayList<>();
    ArrayList<Double> y_rotationvectorCoordinate = new ArrayList<>();
    ArrayList<Double> z_rotationvectorCoordinate = new ArrayList<>();
    ArrayList<Long> rotationvectorNanoTime = new ArrayList<>();

    ArrayList<Double> x_geomagneticrotationvectorCoordinate = new ArrayList<>();
    ArrayList<Double> y_geomagneticrotationvectorCoordinate = new ArrayList<>();
    ArrayList<Double> z_geomagneticrotationvectorCoordinate = new ArrayList<>();
    ArrayList<Long> geomagneticrotationvectorNanoTime = new ArrayList<>();


    /**
     * @param
     */
    public void writeCSVFeaturesFile(String fileName) {

        File sdCard = Environment.getExternalStorageDirectory();
        directory = new File(sdCard.getAbsolutePath() + "/TouchLogger");

        //String csvFile = fileName + ".csv";
        String csvFile = fileName;
        File file = new File(directory, csvFile);

        BufferedReader br = null;
        String cvsSplitBy = ";";

        try {

            br = new BufferedReader(new FileReader(file));
            // int count = 1;
            String next = null, line = br.readLine();
            int count = 1;
            for (boolean first = true, last = (line == null); !last; first = false, line = next) {
                last = ((next = br.readLine()) == null);
                // line = br.readLine();
                if(!line.contains(";") || line.contains("Sensor") || first) {
                    continue;
                }
                String[] keylogger = line.split(cvsSplitBy);
                String key = keylogger[2];
                if (last) {
                    if (!"".equals(key)) {
                        populateCoordinates(keylogger);
                    }

                    populateData(count,x_gravityCoordinate, y_gravityCoordinate, z_gravityCoordinate, gravityNanoTime,
                            "Gravity");
                    populateData(count,x_gyroscopeCoordinate, y_gyroscopeCoordinate, z_gyroscopeCoordinate, gyroscopeNanoTime,
                            "Gyroscope");
                    populateData(count,x_magnitudeCoordinate, y_magnitudeCoordinate, z_magnitudeCoordinate, magnitudeNanoTime,
                            "Magnitude");
                    populateData(count,x_accelerometerCoordinate, y_accelerometerCoordinate, z_accelerometerCoordinate,
                            accelerometerNanoTime, "Accelerometer");
                    populateData(count,x_linearaccelerationCoordinate, y_linearaccelerationCoordinate, z_linearaccelerationCoordinate,
                            linearaccelerationNanoTime, "Linear Acceleration");
                    populateData(count,x_rotationvectorCoordinate, y_rotationvectorCoordinate, z_rotationvectorCoordinate,
                            rotationvectorNanoTime, "Rotation Vector");
                    populateData(count,x_geomagneticrotationvectorCoordinate, y_geomagneticrotationvectorCoordinate, z_geomagneticrotationvectorCoordinate,
                            geomagneticrotationvectorNanoTime, "Geomagnetic Rotation Vector");

                } else if (!"".equals(key)) {

                    if ("".equals(firstKey) || (firstKey).equals(key)) {

                        if ("".equals(firstKey)) {
                            firstKey = key;
                            x = keylogger[3];
                            y = keylogger[4];

                            aux_string = String.valueOf(keylogger[8]);
                            username = String.valueOf(keylogger[9]);
                            layout = String.valueOf(keylogger[10]);
                            orientation = String.valueOf(keylogger[11]);
                            variation = String.valueOf(keylogger[12]);
                            hardware_adds = String.valueOf(keylogger[13]);
                            input = String.valueOf(keylogger[14]);
                            posture = String.valueOf(keylogger[15]);
                            external_factors = String.valueOf(keylogger[16]);

                        }

                        populateCoordinates(keylogger);

                    } else if (!(firstKey).equals(key)) {

                        populateData(count,x_gravityCoordinate, y_gravityCoordinate, z_gravityCoordinate, gravityNanoTime,
                                "Gravity");
                        populateData(count,x_gyroscopeCoordinate, y_gyroscopeCoordinate, z_gyroscopeCoordinate,
                                gyroscopeNanoTime, "Gyroscope");
                        populateData(count,x_magnitudeCoordinate, y_magnitudeCoordinate, z_magnitudeCoordinate,
                                magnitudeNanoTime, "Magnitude");
                        populateData(count,x_accelerometerCoordinate, y_accelerometerCoordinate, z_accelerometerCoordinate,
                                accelerometerNanoTime, "Accelerometer");
                        populateData(count,x_linearaccelerationCoordinate, y_linearaccelerationCoordinate, z_linearaccelerationCoordinate,
                                linearaccelerationNanoTime, "Linear Acceleration");
                        populateData(count,x_rotationvectorCoordinate, y_rotationvectorCoordinate, z_rotationvectorCoordinate,
                                rotationvectorNanoTime, "Rotation Vector");
                        populateData(count, x_geomagneticrotationvectorCoordinate, y_geomagneticrotationvectorCoordinate, z_geomagneticrotationvectorCoordinate,
                                geomagneticrotationvectorNanoTime, "Geomagnetic Rotation Vector");

                        firstKey = key;
                        x = keylogger[3];
                        y = keylogger[4];

                        x_gravityCoordinate.clear();
                        y_gravityCoordinate.clear();
                        z_gravityCoordinate.clear();
                        gravityNanoTime.clear();

                        x_gyroscopeCoordinate.clear();
                        y_gyroscopeCoordinate.clear();
                        z_gyroscopeCoordinate.clear();
                        gyroscopeNanoTime.clear();

                        x_magnitudeCoordinate.clear();
                        y_magnitudeCoordinate.clear();
                        z_magnitudeCoordinate.clear();
                        magnitudeNanoTime.clear();

                        x_accelerometerCoordinate.clear();
                        y_accelerometerCoordinate.clear();
                        z_accelerometerCoordinate.clear();
                        accelerometerNanoTime.clear();

                        x_linearaccelerationCoordinate.clear();
                        y_linearaccelerationCoordinate.clear();
                        z_linearaccelerationCoordinate.clear();
                        linearaccelerationNanoTime.clear();

                        x_rotationvectorCoordinate.clear();
                        y_rotationvectorCoordinate.clear();
                        z_rotationvectorCoordinate.clear();
                        rotationvectorNanoTime.clear();

                        x_geomagneticrotationvectorCoordinate.clear();
                        y_geomagneticrotationvectorCoordinate.clear();
                        z_geomagneticrotationvectorCoordinate.clear();
                        geomagneticrotationvectorNanoTime.clear();

                        count++;
                        aux_string = String.valueOf(keylogger[8]);
                        username = String.valueOf(keylogger[9]);
                        layout = String.valueOf(keylogger[10]);
                        orientation = String.valueOf(keylogger[11]);
                        variation = String.valueOf(keylogger[12]);
                        hardware_adds = String.valueOf(keylogger[13]);
                        input = String.valueOf(keylogger[14]);
                        posture = String.valueOf(keylogger[15]);
                        external_factors = String.valueOf(keylogger[16]);
                        populateCoordinates(keylogger);
                    }
                }
            }

            writeToFile(dataBuffer.toString(), fileName);
            br.close();
        } catch (FileNotFoundException e) {
            Log.e(CLASS_NAME,"FileNotFoundException --->" + e.getMessage());
        } catch (IOException e) {
            Log.e(CLASS_NAME, "IOException --->" + e.getMessage());
        }

        Log.i(CLASS_NAME, "File generated Successfully");
    }

    private void populateCoordinates(String[] keylogger) {
        if ("Gravity".equals(keylogger[0])) {
            x_gravityCoordinate.add(Double.valueOf(keylogger[5]));
            y_gravityCoordinate.add(Double.valueOf(keylogger[6]));
            z_gravityCoordinate.add(Double.valueOf(keylogger[7]));
            gravityNanoTime.add(Long.valueOf(keylogger[1]));

        } else if ("Gyroscope".equals(keylogger[0])) {
            x_gyroscopeCoordinate.add(Double.valueOf(keylogger[5]));
            y_gyroscopeCoordinate.add(Double.valueOf(keylogger[6]));
            z_gyroscopeCoordinate.add(Double.valueOf(keylogger[7]));
            gyroscopeNanoTime.add(Long.valueOf(keylogger[1]));

        } else if ("Magnitude".equals(keylogger[0])) {
            x_magnitudeCoordinate.add(Double.valueOf(keylogger[5]));
            y_magnitudeCoordinate.add(Double.valueOf(keylogger[6]));
            z_magnitudeCoordinate.add(Double.valueOf(keylogger[7]));
            magnitudeNanoTime.add(Long.valueOf(keylogger[1]));

        } else if ("Acceleration".equals(keylogger[0])) {
            x_accelerometerCoordinate.add(Double.valueOf(keylogger[5]));
            y_accelerometerCoordinate.add(Double.valueOf(keylogger[6]));
            z_accelerometerCoordinate.add(Double.valueOf(keylogger[7]));
            accelerometerNanoTime.add(Long.valueOf(keylogger[1]));

        } else if ("Linear Acceleration".equals(keylogger[0])) {
            x_linearaccelerationCoordinate.add(Double.valueOf(keylogger[5]));
            y_linearaccelerationCoordinate.add(Double.valueOf(keylogger[6]));
            z_linearaccelerationCoordinate.add(Double.valueOf(keylogger[7]));
            linearaccelerationNanoTime.add(Long.valueOf(keylogger[1]));

        } else if ("Rotation Vector".equals(keylogger[0])) {
            x_rotationvectorCoordinate.add(Double.valueOf(keylogger[5]));
            y_rotationvectorCoordinate.add(Double.valueOf(keylogger[6]));
            z_rotationvectorCoordinate.add(Double.valueOf(keylogger[7]));
            rotationvectorNanoTime.add(Long.valueOf(keylogger[1]));

        } else if ("Geomagnetic Rotation Vector".equals(keylogger[0])) {
            x_geomagneticrotationvectorCoordinate.add(Double.valueOf(keylogger[5]));
            y_geomagneticrotationvectorCoordinate.add(Double.valueOf(keylogger[6]));
            z_geomagneticrotationvectorCoordinate.add(Double.valueOf(keylogger[7]));
            rotationvectorNanoTime.add(Long.valueOf(keylogger[1]));

        }
    }

    private void populateData(int count,ArrayList<Double> x_coordinate, ArrayList<Double> y_coordinate,
                              ArrayList<Double> z_coordinate, ArrayList<Long> nanoTime, String name) {

        if (x_coordinate.size()==0 || y_coordinate.size()==0 || z_coordinate.size()==0){
            return;
        }
        x_mean = getMean(x_coordinate);
        x_variance = getVariance(x_coordinate, x_mean);
        x_stdDev = getStdDev(x_variance);
        Collections.sort(x_coordinate);
        x_minValue = x_coordinate.get(0);
        x_maxValue = x_coordinate.get(x_coordinate.size() - 1);

        y_mean = getMean(y_coordinate);
        y_variance = getVariance(y_coordinate, y_mean);
        y_stdDev = getStdDev(y_variance);
        Collections.sort(y_coordinate);
        y_minValue = y_coordinate.get(0);
        y_maxValue = y_coordinate.get(y_coordinate.size() - 1);

        z_mean = getMean(z_coordinate);
        z_variance = getVariance(z_coordinate, z_mean);
        z_stdDev = getStdDev(z_variance);
        Collections.sort(z_coordinate);
        z_minValue = z_coordinate.get(0);
        z_maxValue = z_coordinate.get(z_coordinate.size() - 1);

        Collections.sort(nanoTime);
        timeduration = nanoTime.get(nanoTime.size() - 1) - nanoTime.get(0);

        dataBuffer.append(count).append(";").append(name).append(";").append(System.nanoTime()).append(";")
                .append(firstKey).append(";").append(aux_string).append(";").append(username).append(";").append(layout).append(";")
                .append(orientation).append(";").append(variation).append(";").append(hardware_adds).append(";")
                .append(input).append(";").append(posture).append(";").append(external_factors).append(";").append(x)
                .append(";").append(y).append(";").append(timeduration).append(";").append(x_mean).append(";")
                .append(x_variance).append(";").append(x_stdDev).append(";").append(x_minValue).append(";")
                .append(x_maxValue).append(";").append(y_mean).append(";").append(y_variance).append(";")
                .append(y_stdDev).append(";").append(y_minValue).append(";").append(y_maxValue).append(";")
                .append(z_mean).append(";").append(z_variance).append(";").append(z_stdDev).append(";")
                .append(z_minValue).append(";").append(z_maxValue).append(";").append("\r\n");

    }

    /**
     * Writing the Data to File to ExternalStorageDirectory
     *
     * @param data
     *            data which contains the Logger values
     */
    private boolean writeToFile(String data, String fileName) {
        // Log.i(CLASS_NAME,"data--------->"+data);
        // boolean isNewFile = false;
        boolean isFinished = false;
        try {

            File file = new File(directory,fileName + "_withfeatures.csv");
            FileOutputStream fos;
            if (file.exists()) {
                file.delete();
            }
            try {
                fos = new FileOutputStream(file, true);
                // System.out.println(CLASS_NAME, "Data is append to the File "
                // + fileName);
            } catch (FileNotFoundException fileNotFoundException) {
                fos = new FileOutputStream(file);
                // System.out.println(CLASS_NAME, "File " + fileName + " is
                // created with new data");
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(HEADERS + "\r\n" + data);
            outputStreamWriter.close();
            isFinished = true;

        } catch (IOException e) {
            System.out.println("File write failed: " + e.toString());
        }
        return isFinished;
    }

    /**
     * get the means for the given Arraylist
     *
     * @param data arraylist
     * @return
     */
    private static double getMean(ArrayList<Double> data) {
        int size = data.size();
        double sum = 0;

        if (size == 1) {
            return data.iterator().next();
        } else {
            for (double a : data) {
                sum += a;
            }
            return sum / size;
        }

    }

    /**
     * get the variancee for the given Arraylist
     *
     * @param data arraylist
     * @param mean mean value which is generated
     * @return
     */
    private static double getVariance(ArrayList<Double> data, double mean) {
        // double mean = getMean();
        int size = data.size();
        double temp = 0;

        if (size == 1) {
            double a = data.iterator().next();
            temp = (mean - a) * (mean - a);
        } else {
            for (double a : data) {
                temp += (mean - a) * (mean - a);
            }
        }
        return temp / size;
    }

    private static double getStdDev(double variance) {
        return Math.sqrt(variance);
    }

}

