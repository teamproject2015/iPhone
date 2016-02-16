package android.android_5.touchlogger;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import android.android_5.R;
import android.android_5.sensors.SensorManagerActivity;

public class PinCodeActivity extends SensorManagerActivity {

    private static final String FILENAME = "PinCodeTouchLogger.csv";
    private static final String CLASS_NAME = PinCodeActivity.class.getName();
    private static String NUMERIC_RANDOMLETTERS = "0123456789";
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchlogger_pincode);
        generateRandomKey(NUMERIC_RANDOMLETTERS);
        callToolBar();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        getBundleData();

        View view = this.findViewById(android.R.id.content);
        Keyboard mKeyboard = new Keyboard(this, R.xml.pinkeyboard);

        KeyboardView mKeyboardView = callCustomKeyboard(view,mKeyboard,R.id.keyboardview);
        // Install the key handler
        mKeyboardView.setOnKeyboardActionListener(new KeyboardActionListener() {

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                //Here check the primaryCode to see which key is pressed
                //based on the android:codes property
                View focusCurrent = PinCodeActivity.this.getWindow().getCurrentFocus();
                if (focusCurrent == null) return;
                EditText edittext = (EditText) focusCurrent;
                Editable editable = edittext.getText();
                int start = edittext.getSelectionStart();

                if (primaryCode == Keyboard.KEYCODE_DELETE) {
                    if (editable != null && start > 0)
                        editable.delete(start - 1, start);
                } else {
                    //Log.i(CLASS_NAME, "You just pressed " + primaryCode + " button");
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            }

        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
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

        EditText keyValue = (EditText) findViewById(R.id.editText_key);
        //Toast.makeText(getApplicationContext(), "keyValue-->" + keyValue.getText().toString(), Toast.LENGTH_SHORT).show();
       /* if(event.getAction() == MotionEvent.ACTION_DOWN) {
            downTime = SystemClock.elapsedRealtime();
        }*/
        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                //this is the time in milliseconds
                long upTime = System.currentTimeMillis();

                if (keyValue.getText() != null
                        && !"".equals(keyValue.getText().toString())
                        && recordTouchEvent(event, keyValue.getText().toString(),0,upTime,null,null)) {

                        count++;
                        if (count == KEYSTROKE_COUNT) {
                            writeToFile(logValues.toString(), FILENAME);
                            Toast.makeText(getApplicationContext(),
                                    "Key Stocks Saved",
                                    Toast.LENGTH_SHORT).show();
                            logValues.setLength(0);
                            count = 0;
                        }

                        //final EditText textMessage = (EditText) findViewById(R.id.editText_key);
                        //Log.i(CLASS_NAME, "generatedKey value = " + generatedKey);
                        TextWatcher tw = new TextWatcher() {
                            public void afterTextChanged(Editable s) {
                                generateRandomKey(NUMERIC_RANDOMLETTERS);
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        };
                        keyValue.addTextChangedListener(tw);
                        keyValue.setText("");
                        keyValue.removeTextChangedListener(tw);
                }


                break;
        }
        return true;
    }

}
