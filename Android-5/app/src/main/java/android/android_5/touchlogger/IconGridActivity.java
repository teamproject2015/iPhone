package android.android_5.touchlogger;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.android_5.R;
import android.android_5.pojo.Card;
import android.android_5.sensors.SensorManagerActivity;

public class IconGridActivity extends SensorManagerActivity {

    private static final String FILENAME = "IconGridTouchLogger.csv";
    private static final String CLASS_NAME = "IconGridActivity";
    private static int ROW_COUNT = 5;
    private static int COL_COUNT = 3;
    private static Object lock = new Object();
    int turns;
    private List<Drawable> images;
    private Context context;
    private Drawable backImage;
    private int[][] cards;
    private Card firstCard;
    private Card secondCard;
    private UpdateCardsHandler handler;
    private TouchListener touchListener;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchlogger_icongrid);
        callToolBar();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        loadImages();

        getBundleData();

        handler = new UpdateCardsHandler();
        backImage = getResources().getDrawable(R.drawable.ic_memgame_back);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.LinearLayout_iconMatch);
        context = mainLayout.getContext();
        touchListener = new TouchListener();

        cards = new int[COL_COUNT][ROW_COUNT];

        for (int y = 0; y < ROW_COUNT; y++) {
            mainLayout.addView(createRow(y));
        }

        firstCard = null;
        loadCards();
        turns = 0;
        ((TextView) findViewById(R.id.textView_icongrid_tries)).setText("Tries: " + turns);
    }

    /**
     * @param y
     * @return
     */
    private LinearLayout createRow(int y) {
        LinearLayout row = new LinearLayout(context);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        row.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        for (int x = 0; x < COL_COUNT; x++) {
            row.addView(createImageButton(x, y, rowParam));
        }
        return row;
    }

    private View createImageButton(int x, int y, LinearLayout.LayoutParams rowParam) {
        ImageButton button = new ImageButton(context);
        button.setImageDrawable(backImage);
        button.setScaleType(ImageView.ScaleType.FIT_CENTER);
        button.setLayoutParams(rowParam);
        button.setId(100 * x + y);
        button.setBackgroundResource(R.drawable.imagebutton_border);
        String grid = "GRID_" + y + "" + x;

        for (IconGridEnum iconGridEnum : IconGridEnum.values()) {
            //Log.d(CLASS_NAME,"grid-->"+grid+"iconGridEnum-->"+iconGridEnum);
            if (grid.equals(iconGridEnum.toString())) {
                //Log.d(CLASS_NAME,"button.getTag-->"+iconGridEnum.getValue());
                button.setTag(iconGridEnum.getValue());
                break;
            }
        }
        button.setOnTouchListener(touchListener);
        return button;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    private void loadCards() {
        try {
            int size = ROW_COUNT * COL_COUNT;
            Log.i("loadCards()", "size=" + size);
            ArrayList<Integer> list = new ArrayList<Integer>();

            for (int i = 0; i < size; i++) {
                list.add(i);
            }

            Random r = new Random();
            for (int i = size - 1; i >= 0; i--) {
                int t = 0;
                if (i > 0) {
                    t = r.nextInt(i);
                }
                t = list.remove(t);
                cards[i % COL_COUNT][i / COL_COUNT] = t % (size / 2);
                Log.i("loadCards()", "card[" + (i % COL_COUNT) +
                        "][" + (i / COL_COUNT) + "]=" + cards[i % COL_COUNT][i / COL_COUNT]);
            }
        } catch (Exception e) {
            Log.e("loadCards()", e + "");
        }
    }

    private void loadImages() {
        images = new ArrayList<Drawable>();
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_1));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_2));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_3));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_4));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_5));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_6));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_7));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_8));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_9));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_10));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_11));
        images.add(getResources().getDrawable(R.drawable.ic_icongrid_12));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    class UpdateCardsHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            synchronized (lock) {
                checkCards();
            }
        }

        public void checkCards() {
            if (cards[secondCard.x][secondCard.y] == cards[firstCard.x][firstCard.y]) {
                firstCard.button.setVisibility(View.INVISIBLE);
                secondCard.button.setVisibility(View.INVISIBLE);
            } else {
                secondCard.button.setImageDrawable(backImage);
                secondCard.button.setBackgroundResource(R.drawable.imagebutton_border);
                firstCard.button.setImageDrawable(backImage);
                firstCard.button.setBackgroundResource(R.drawable.imagebutton_border);
            }
            firstCard = null;
            secondCard = null;
        }
    }

    /**
     * When the User press/ touch the screen the event from OnTouchevent will be
     * triggering the onTouch method, the method will save the x,y coordinates
     * and accelerometer and orientation coordinates
     */
    class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            synchronized (lock) {
                if (firstCard != null && secondCard != null) {
                    return false;
                }
                int id = v.getId();
                int x = id / 100;
                int y = id % 100;
                turnCard((ImageButton) v, x, y);
            }

            int key = (int) v.getTag();

            if (key != 999) {
                generatedKey = String.valueOf(key);
                recordTouchEvent(event, null,0,0,null,null);

                count++;
                if (count == KEYSTROKE_COUNT) {
                    writeToFile(logValues.toString(), FILENAME);
                    Toast.makeText(getApplicationContext(),
                            "Key Stocks Saved",
                            Toast.LENGTH_SHORT).show();
                    logValues.setLength(0);
                    count = 0;
                }
            }
            return true;
        }

        private void turnCard(ImageButton button, int x, int y) {
            button.setImageDrawable(images.get(cards[x][y]));
            button.setBackgroundResource(R.drawable.imagebutton_border);

            if (firstCard == null) {
                firstCard = new Card(button, x, y);
            } else {
                if (firstCard.x == x && firstCard.y == y) {
                    return; //the user pressed the same card
                }

                secondCard = new Card(button, x, y);
                turns++;
                ((TextView) findViewById(R.id.textView_icongrid_tries)).setText("Tries: " + turns);

                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            synchronized (lock) {
                                handler.sendEmptyMessage(0);
                            }
                        } catch (Exception e) {
                            Log.e("E1", e.getMessage());
                        }
                    }
                };
                Timer t = new Timer(false);
                t.schedule(tt, 1300);
            }
        }
    }
}
