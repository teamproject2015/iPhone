package android.android_5.touchlogger;

import android.content.pm.ActivityInfo;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import android.android_5.R;
import android.android_5.sensors.SensorManagerActivity;


public class KeyboardActivity extends SensorManagerActivity {

    private static final String FILENAME = "KeyboardTouchLogger.csv";

    private static final String CLASS_NAME = KeyboardActivity.class.getName();

    private static String ALPHANUMERIC_RANDOMLETTERS = "abcdefghijklmnopqrstuvwxyz";

    private int count;
    private boolean mCapsLock;
    private long mLastShiftTime;

    private Keyboard mQwertyKeyboard;
    private Keyboard mSymbolsKeyboard;
    private Keyboard mSymbolsShiftedKeyboard;
    private KeyboardView mKeyboardView;
    private TextView typedKeyTextView;
    private long downTime;
    private Date beginningTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchlogger_keyboard);
        generateRandomKey(ALPHANUMERIC_RANDOMLETTERS);
        callToolBar();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        int orientation = bundle.getInt("orientationPosition");
        typedKeyTextView = (TextView) findViewById(R.id.textView_typedKey);

        getBundleData();

        Log.d(CLASS_NAME, "orientation-->" + orientation);
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }

        View view = this.findViewById(android.R.id.content);

        // Create the Keyboard
        mQwertyKeyboard = new Keyboard(this, R.xml.customkeyboard);
        mSymbolsKeyboard = new Keyboard(this, R.xml.customkeyboard_symbols);
        mSymbolsShiftedKeyboard = new Keyboard(this, R.xml.customkeyboard_shift);

        mKeyboardView = callCustomKeyboard(view,mQwertyKeyboard,R.id.keyboardview);
        // Install the key handler
        mKeyboardView.setOnKeyboardActionListener(new KeyboardActionListener() {

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                //Here check the primaryCode to see which key is pressed
                //based on the android:codes property
                View focusCurrent = KeyboardActivity.this.getWindow().getCurrentFocus();
                if (focusCurrent == null) return;
                EditText edittext = (EditText) focusCurrent;
                Editable editable = edittext.getText();
                int start = edittext.getSelectionStart();


                if (primaryCode == Keyboard.KEYCODE_DELETE) {
                    handleBackspace(editable, start);
                } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                    handleShift();
                } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                        && mKeyboardView != null) {
                    Keyboard current = mKeyboardView.getKeyboard();
                    if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
                        current = mQwertyKeyboard;
                    } else {
                        current = mSymbolsKeyboard;
                    }
                    mKeyboardView.setKeyboard(current);
                    if (current == mSymbolsKeyboard) {
                        current.setShifted(false);
                    }
                } else {
                    handleCharacter(primaryCode, keyCodes, editable, start);
                }
            }

        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }





    private void handleBackspace(Editable editable, int start) {
        if (editable == null)
            return;
        if (start > 0) {
            editable.delete(start - 1, start);
            typedKeyTextView.setText(editable.toString());
        }
    }

    private void handleShift() {
        if (mKeyboardView == null) {
            return;
        }
        Keyboard currentKeyboard = mKeyboardView.getKeyboard();
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            mKeyboardView.setShifted(mCapsLock || !mKeyboardView.isShifted());
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            mKeyboardView.setKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            mKeyboardView.setKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
    }

    private void handleCharacter(int primaryCode, int[] keyCodes,Editable editable,int start) {

        if (mKeyboardView.isShifted()) {
            primaryCode = Character.toUpperCase(primaryCode);
        }

        editable.insert(start, Character.toString((char) primaryCode));
        typedKeyTextView.setText(editable.toString());
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    private class SaveCSVFile extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
          return writeToFile(params[0], FILENAME);
        }

        @Override
        protected void onPostExecute(Boolean isFinished) {
            if(isFinished) {
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
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            downTime = SystemClock.elapsedRealtime();
            beginningTimestamp = new Date();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {

            //case MotionEvent.ACTION_UP:
            //this is the time in milliseconds
            long upTime = SystemClock.elapsedRealtime();
            Date endingTimestamp = new Date();

            if (keyValue.getText() != null
                    && !"".equals(keyValue.getText().toString())) {

                recordTouchEvent(event,keyValue.getText().toString(),downTime,upTime,beginningTimestamp,endingTimestamp);
                count++;
                if (count == KEYSTROKE_COUNT) {
                    /*writeToFile(logValues.toString(), FILENAME);
                    Toast.makeText(getApplicationContext(),
                            "Key Stocks Saved",
                            Toast.LENGTH_SHORT).show();*/
                    SaveCSVFile saveCSVFile = new SaveCSVFile();
                    saveCSVFile.execute(logValues.toString());
                    logValues.setLength(0);;
                    count = 0;
                }

                //final EditText textMessage = (EditText) findViewById(R.id.editText_key);
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
                keyValue.removeTextChangedListener(tw);
            }
        }




        return true;
    }
}
