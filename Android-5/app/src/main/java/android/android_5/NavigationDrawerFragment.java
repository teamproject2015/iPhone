package android.android_5;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.android_5.adapter.NavigationAdapter;
import android.android_5.graphs.AccelerometerActivity;
import android.android_5.pojo.NavigationList;
import android.android_5.touchlogger.TouchloggerActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    private static final String PREF_FILE_NAME = "saveDrawerPref";

    private static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private boolean mUserLearnDrawer;

    private boolean mFromSavedInstancedstate;

    private View containerView;

    private RecyclerView recyclerView;

    private NavigationAdapter navigationAdapter;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    /**
     * Generating the Navigation View from the data provided in this method
     *
     * @return List<NavigationList>
     */
    public static List<NavigationList> getData() {
        List<NavigationList> navigationLists = new ArrayList<NavigationList>();
        int icons[] = {R.drawable.ic_home_black_24dp, R.drawable.ic_school_black_24dp, R.drawable.ic_settings_black_24dp, R.drawable.ic_info_black_24dp,R.drawable.ic_info_black_24dp};

        String title[] = {"Home", "start TouchLogger", "settings", "About Us","graph"};

        int count = 0;
        for (int index = 0; index < title.length; index++) {
            NavigationList navigationList = new NavigationList();
            navigationList.setId(icons[index]);
            navigationList.setTitle(title[index]);
            navigationLists.add(navigationList);
        }
        return navigationLists;
    }

    public static void savePreference(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();

    }

    public static String readFromPreference(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnDrawer = Boolean.valueOf(readFromPreference(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstancedstate = true;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);


        recyclerView = (RecyclerView) layout.findViewById(R.id.recycleView_List);
        navigationAdapter = new NavigationAdapter(getActivity(), getData());
        recyclerView.setAdapter(navigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    Intent i = new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getActivity(), TouchloggerActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                } else if (position == 2) {
                    Toast.makeText(getActivity(), "under progress, working on it", Toast.LENGTH_LONG).show();
                } else if (position == 3) {
                    Intent i = new Intent(getActivity(), AboutActivity.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(getActivity(), AccelerometerActivity.class);
                    startActivity(i);
                }
                mDrawerLayout.closeDrawers();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return layout;

    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);

        mDrawerLayout = drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnDrawer) {
                    mUserLearnDrawer = true;
                    savePreference(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnDrawer + "");
                }

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
            /*
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset < 0.6) {
                    toolbar.setAlpha(1 - slideOffset);
                }
            }*/
        };

        /*if (!mUserLearnDrawer && !mFromSavedInstancedstate) {
            mDrawerLayout.openDrawer(containerView);
        } */

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;

        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
