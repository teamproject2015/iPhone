package android.android_5.sensors.sensors;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class AccelerometerResultReceiver extends ResultReceiver {

    public static final int RESULTCODE_ERROR = -1;
    public static final String EXTRA_ERRORMSG = "AccelerometerResultReceiver.ERRORMSG";

    public static final int RESULTCODE_UPDATE = 1;
    public static final String EXTRA_X = "AccelerometerResultReceiver.X";
    public static final String EXTRA_Y = "AccelerometerResultReceiver.Y";
    public static final String EXTRA_Z = "AccelerometerResultReceiver.Z";

    private Receiver mReceiver;

    public AccelerometerResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        public void newEvent(float x, float y, float z);

        public void error(String error);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //Log.i("Receiver", "entering the onReceiveResult ");
        if (mReceiver != null) {
            if (resultCode == RESULTCODE_ERROR) {
                mReceiver.error(resultData.getString(EXTRA_ERRORMSG));
            } else {
                float x = resultData.getFloat(EXTRA_X);
                float y = resultData.getFloat(EXTRA_Y);
                float z = resultData.getFloat(EXTRA_Z);
                //Log.i("AcceReceiver", "x=" + x + ",y=" + y + ",z=" + z);
                mReceiver.newEvent(x, y, z);
            }
        }
        //Log.i("Receiver", "exiting the onReceiveResult ");
    }

}
