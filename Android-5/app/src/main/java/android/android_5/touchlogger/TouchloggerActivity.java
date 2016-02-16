package android.android_5.touchlogger;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import android.android_5.BaseActivity;
import android.android_5.NavigationDrawerFragment;
import android.android_5.R;

/**
 *         TouchloggerActivity Class is used to record the Logger activities to train
 *         the keystoke for Keyboard
 */
public class TouchloggerActivity extends BaseActivity {


    private static final String CLASS_NAME = "TouchloggerActivity";


    /**
     * This method overrides the default onCreate method for Super Class Activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchlogger);

        Toolbar toolbar = callToolBar();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationDrawerFragment drawerFragment
                = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.main_drawerLayout), toolbar);


        Button buttonContinue = (Button) findViewById(R.id.button_touchLoggerContinue);
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
                    int position = 0 ;
                    if (orientationPosition == 0) {
                        position = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    } else if (orientationPosition == 1) {
                        position = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    } else if (orientationPosition == 2) {
                        position = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    }
                    Intent i = new Intent(getApplicationContext(), KeyboardActivity.class);
                    //Create the bundle

                    bundle.putInt("orientationPosition", position);
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (layoutPosition == 1) {
                    Log.d(CLASS_NAME, layoutPosition + " is selected");
                    Intent i = new Intent(getApplicationContext(), IconGridActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (layoutPosition == 2) {
                    Log.d(CLASS_NAME, layoutPosition + " is selected");
                    Intent i = new Intent(getApplicationContext(), PinCodeActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });

    }


}