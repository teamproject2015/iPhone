package android.android_5;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class BaseActivity extends AppCompatActivity {

    private static final String CLASS_NAME = "BaseActivity";

    protected static final int KEYSTROKE_COUNT = 40;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public Toolbar callToolBar() {
        Toolbar tlBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(tlBar);
        return tlBar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Writing the Data to File to ExternalStorageDirectory
     *
     * @param data data which contains the Logger values
     */
    protected boolean writeToFile(String data, String fileName) {
        //Log.i(CLASS_NAME,"data--------->"+data);
        boolean isNewFile = false;
        boolean isFinished = false;
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            Log.i(CLASS_NAME, "sdCard-->" + sdCard.getAbsolutePath());
            File directory = new File(sdCard.getAbsolutePath() + "/TouchLogger");
            directory.mkdirs();
            File file = new File(directory, fileName);
            FileOutputStream fos;
            if(!file.exists()) {
                isNewFile = true;
            }
            try {
                fos = new FileOutputStream(file, true);
                Log.d(CLASS_NAME, "Data is append to the File " + fileName);
            } catch (FileNotFoundException fileNotFou0ndException) {
                fos = new FileOutputStream(file);
                Log.d(CLASS_NAME, "File " + fileName + " is created with new data");
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);

            if(isNewFile) {
                outputStreamWriter.write(getString(R.string.file_headerfields)+"\r\n"+data);
            } else {
                outputStreamWriter.write(data);
            }
            outputStreamWriter.close();
            isFinished = true;

        } catch (IOException e) {
            Log.e(CLASS_NAME, "File write failed: " + e.toString());
        }
        return isFinished;
    }



    protected String readFromFile(String fileName) {
        String ret = "";
        try {
            InputStream inputStream = openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e(CLASS_NAME, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(CLASS_NAME, "Can not read file: " + e.toString());
        }

        return ret;
    }


    protected KeyboardView callCustomKeyboard(View view,Keyboard mKeyboard,int keyboardViewId ) {

        // Lookup the KeyboardView
        KeyboardView mKeyboardView = (KeyboardView) findViewById(keyboardViewId);
        // Attach the customkeyboard to the view
        mKeyboardView.setKeyboard(mKeyboard);

        mKeyboardView.setVisibility(View.VISIBLE);

        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);

        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);

        EditText edittext = (EditText) findViewById(R.id.editText_key);

        // Disable standard customkeyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)'
        // (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard customkeyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        return mKeyboardView;
    }

    /**
     *c This method returns the Bundle value for the @param value
     * @param bundle Bundle Object Should be passed
     * @param value Key for the Bundle
     * @return
     */
    protected String getBundle(Bundle bundle,String value) {
       return bundle.getString(value);
    }


}
