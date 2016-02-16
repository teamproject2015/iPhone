package android.android_5.sensors.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.view.Display;
import android.view.WindowManager;

import android.android_5.utils.Constants;
import android.android_5.utils.Vector3;

public class OrientationService extends Service implements SensorEventListener {

	private ResultReceiver mReceiver;
	private SensorManager sensorManager;

	Vector3 gravity;
	protected float[] magnitude = new float[3];
	private float RTmp[] = new float[9];
	private float Rot[] = new float[9];
	private float I[] = new float[9];

	protected float[] orientation = new float[3];

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mReceiver = intent.getParcelableExtra(Constants.EXTRA_RECEIVER);

		return START_STICKY;

	}

	@Override
	public void onCreate() {
		// Setup and start collecting
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_FASTEST);
		super.onCreate();
		gravity = new Vector3();
	}

	@Override
	public void onDestroy() {
		// Stop collecting and tear down
		sensorManager.unregisterListener(this);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private final String TAG = OrientationService.class.getSimpleName();

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int type = event.sensor.getType();

		if (type == Sensor.TYPE_GRAVITY) {
			gravity.z = event.values[0];
			gravity.x = event.values[1];
			gravity.y = event.values[2];
		}
		if (type == Sensor.TYPE_MAGNETIC_FIELD) {
			magnitude = event.values;
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
			//Log.i("OrientationService", orientation[0]+","+orientation[1]+","+orientation[2]);
			if (mReceiver != null) {
				Bundle result = new Bundle();
				result.putFloat(OrientationResultReceiver.EXTRA_X, orientation[0]);
				result.putFloat(OrientationResultReceiver.EXTRA_Y, orientation[1]);
				result.putFloat(OrientationResultReceiver.EXTRA_Z, orientation[2]);
				mReceiver.send(OrientationResultReceiver.RESULTCODE_UPDATE,
						result);
			}
            /*orientation[0] = (float) (((results[0] * 180) / Math.PI) + 180); //azimuth
            //Positive Roll is defined when the phone starts by laying flat
            // on a table and the positive Z-axis begins to tilt towards the positive X-axis.
            orientation[1] = (float) (((results[1] * 180 / Math.PI)) + 90); //pitch
            //Positive Pitch is defined when the phone starts by laying flat
            // on a table and the positive Z-axis begins to tilt towards the positive Y-axis.
            orientation[2] = (float) (((results[2] * 180 / Math.PI))); //roll*/
		}
	}

}
