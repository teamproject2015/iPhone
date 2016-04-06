package de.unimannheim.touchlogger;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private static final String CLASS_NAME = "TouchloggerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Button buttonContinue = (Button) findViewById(R.id.button_touchLoggerContinue);
        final EditText userName = (EditText) findViewById(R.id.editText_userName);
        final Spinner layout = (Spinner) findViewById(R.id.spinner_layout);
        final Spinner orientation = (Spinner) findViewById(R.id.spinner_orientation);
        final Spinner variation = (Spinner) findViewById(R.id.spinner_variation);
        final Spinner hardwareAddons = (Spinner) findViewById(R.id.spinner_addons);
        final Spinner input = (Spinner) findViewById(R.id.spinner_input);
        final Spinner posture = (Spinner) findViewById(R.id.spinner_posture);
        final Spinner externalFactors = (Spinner) findViewById(R.id.spinner_externalFactors);


        final TextView textViewLayout = (TextView) findViewById(R.id.textView_orientation);

        layout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (orientation != null) {
                    if (position != 0) {
                        orientation.setVisibility(View.INVISIBLE);
                        textViewLayout.setVisibility(View.INVISIBLE);
                    } else {
                        orientation.setVisibility(View.VISIBLE);
                        textViewLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Layout values
                 * 0. Keyboard
                 * 1. Icon Grid
                 * 2. PIN code
                 */
                int layoutPosition = layout.getSelectedItemPosition();
                Log.d(CLASS_NAME, "------------" + layoutPosition + "------------------");
                Bundle bundle = new Bundle();
                //Add your data to bundle
                bundle.putString("username", userName.getText().toString());
                bundle.putString("layout", layout.getSelectedItem().toString());
                bundle.putString("orientation", orientation.getSelectedItem().toString());
                bundle.putString("variation", variation.getSelectedItem().toString());
                bundle.putString("hardwareaddons", hardwareAddons.getSelectedItem().toString());
                bundle.putString("input", input.getSelectedItem().toString());
                bundle.putString("posture", posture.getSelectedItem().toString());
                bundle.putString("externalfactors", externalFactors.getSelectedItem().toString());

                if (layoutPosition == 0) {
                    /**
                     * Orientation values
                     *
                     * 0. portrait
                     * 1. Left Orientation
                     * 2. Right Orientation
                     */
                    int orientationPosition = orientation.getSelectedItemPosition();
                    Log.d(CLASS_NAME, layoutPosition + " and " + orientationPosition + " is selected");
                    int position = 0;
                    if (orientationPosition == 0) {
                        position = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    } else if (orientationPosition == 1) {
                        position = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    } else if (orientationPosition == 2) {
                        position = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    }
                    //setContentView(R.layout.activity_touchlogger_keyboard);
                    Intent i = new Intent(getApplicationContext(), KeyboardActivity.class);
                    //Create the bundle

                    bundle.putInt("orientationPosition", position);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });

    }


}
