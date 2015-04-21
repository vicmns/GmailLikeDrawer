package com.vicmns.demogmaillikedrawer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.vicmns.demogmaillikedrawer.lib.ActionBarSlidingPanelToggle;
import com.vicmns.demogmaillikedrawer.lib.CrossFadeSlidingPanelLayout;
import com.vicmns.demogmaillikedrawer.model.DrawerItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    private static final String TAG = "NavigationDrawer";
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private LinearLayout mPeekDrawerLayout;
    private View mFragmentContainerView;
    private ExpandedDrawerListAdapter mAdapter;

    private int mCurrentSelectedPosition = 1;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private List<DrawerItemModel> mDrawerItemModels;
    private List<View> alphaAnimationViews;

    private View headerLayout;
    private View headerImageView;
    private View headerSpinner;
    private int headerImageViewOriginalBottomMargin;
    private int headerImageViewSize;
    private int smallHeaderImageViewSize = 36;
    private int headerOriginalHeight;
    private int currentHeaderHeight;

    private View.OnClickListener peekDrawerViewItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewIndex = (int) v.getTag();
            selectItem(viewIndex + 1);
        }
    };


    private CrossFadeSlidingPanelLayout.CrossFadeSlidingPanelListener mSlidingListener = new CrossFadeSlidingPanelLayout.CrossFadeSlidingPanelListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset, boolean isOpening) {
            if(slideOffset >= 0.25 ) {
                float cAlphaValue = (slideOffset - 0.25f) + (slideOffset / 4);
                setAlphaForViews(cAlphaValue);

                float newHeaderImageSize = smallHeaderImageViewSize + (cAlphaValue * 30);
                float newHeaderMargin = 10 + (cAlphaValue * 35);

                newHeaderImageSize = (newHeaderImageSize > headerImageViewSize) ?
                        headerImageViewSize : newHeaderImageSize;

                setHeaderImageViewSize((int) getPxFromDp(newHeaderImageSize));
                //setHeaderImageViewMargins((int) getPxFromDp(newHeaderMargin));
            }
            else {
                setAlphaForViews(0);
                setHeaderImageViewSize((int) getPxFromDp(smallHeaderImageViewSize));
                //setHeaderImageViewMargins((int) getPxFromDp(10));
            }

            currentHeaderHeight = (int) (getPxFromDp(headerOriginalHeight) * (slideOffset + 0.5f));
            if(currentHeaderHeight > getPxFromDp(headerOriginalHeight)) {
                currentHeaderHeight = (int) getPxFromDp(headerOriginalHeight);
                headerSpinner.setVisibility(View.VISIBLE);
            } else {
                headerSpinner.setVisibility(View.GONE);
            }


            if(currentHeaderHeight < (int) getPxFromDp(84)) currentHeaderHeight = (int) getPxFromDp(84);

            AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) headerLayout.getLayoutParams();
            layoutParams.height = currentHeaderHeight;
            headerLayout.requestLayout();

            if(isOpening) {
            } else {

            }
        }
    };

    private void setAlphaForViews(float value) {
        for(View cView: alphaAnimationViews) {
            if(cView == null) continue;

            cView.setAlpha(value);
        }
        headerSpinner.setAlpha(value);
    }

    private void setHeaderImageViewSize(int size) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) headerImageView.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        headerImageView.requestLayout();
    }

    private void setHeaderImageViewMargins(int margin){
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) headerImageView.getLayoutParams();
        layoutParams.bottomMargin = margin;
        headerImageView.requestLayout();

    }

    private float getPxFromDp(float dp) {
        Resources r = getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    private void getListViewVisibleItems() {
        alphaAnimationViews = new ArrayList<>();
        int visibleChildCount = (mDrawerListView.getLastVisiblePosition() - mDrawerListView.getFirstVisiblePosition()) + 1;
        for(int i = mDrawerListView.getFirstVisiblePosition(); i < mDrawerListView.getLastVisiblePosition(); i++) {
            if(mAdapter.getItem(i).getItemType()
                    != DrawerItemModel.ItemType.MAIN_ITEM) {
                alphaAnimationViews.add(mDrawerListView.getChildAt(i + 1));
            } else {
                alphaAnimationViews.add(mDrawerListView.getChildAt(i + 1).findViewById(R.id.drawer_list_item_section_text));
            }
        }
    }

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        mDrawerItemModels = new ArrayList<>(4);
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_home, "Home", DrawerItemModel.ItemType.MAIN_ITEM));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_explore, "What's near", DrawerItemModel.ItemType.MAIN_ITEM) );
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_shopping_cart, "My cart",DrawerItemModel.ItemType.MAIN_ITEM));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_store, "The candy shop", DrawerItemModel.ItemType.MAIN_ITEM));

        mDrawerItemModels.add(new DrawerItemModel(-1, "", DrawerItemModel.ItemType.SIMPLE_SEPARATOR));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_label, "Extra Item 1", DrawerItemModel.ItemType.SECONDARY_ITEM));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_label, "Extra Item 2", DrawerItemModel.ItemType.SECONDARY_ITEM));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_label, "Extra Item 3", DrawerItemModel.ItemType.SECONDARY_ITEM));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_label, "Extra Item 4", DrawerItemModel.ItemType.SECONDARY_ITEM));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_label, "Extra Item 5", DrawerItemModel.ItemType.SECONDARY_ITEM));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_label, "Extra Item 6", DrawerItemModel.ItemType.SECONDARY_ITEM));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_label, "Extra Item 7", DrawerItemModel.ItemType.SECONDARY_ITEM));
        mDrawerItemModels.add(new DrawerItemModel(R.drawable.ic_label, "Extra Item 8", DrawerItemModel.ItemType.SECONDARY_ITEM));

        if(parentView instanceof ListView) {
            mDrawerListView = (ListView) parentView;
        } else {
            //Tablet layout
            initTabletDrawer(parentView);
            initPeekDrawer();
        }

        initExpandedDrawerList();

        // Select either the default item (1) or the last selected item.
        selectItem(mCurrentSelectedPosition);


        return parentView;
    }

    private void initTabletDrawer(View parentView) {
        mPeekDrawerLayout = (LinearLayout) parentView.findViewById(R.id.peekMenuLayout);
        mDrawerListView = (ListView) parentView.findViewById(R.id.navigationMenuListView);
    }

    private void initExpandedDrawerList() {
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mAdapter = new ExpandedDrawerListAdapter(getActivity(), mDrawerItemModels);

        mDrawerListView.setAdapter(mAdapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        headerLayout = inflater.inflate(R.layout.expanded_drawer_header_item, mDrawerListView, false);
        headerImageView = headerLayout.findViewById(R.id.drawer_profile_picture);
        headerSpinner = headerLayout.findViewById(R.id.bottom_header_view);

        ViewTreeObserver vto = headerLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                headerLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                headerOriginalHeight = headerLayout.getMeasuredHeight();
            }
        });

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) headerImageView.getLayoutParams();
        headerImageViewSize = layoutParams.width;
        headerImageViewOriginalBottomMargin = layoutParams.bottomMargin;

        addProfilePicture((ImageView) headerImageView);
        headerSpinner.setVisibility(View.GONE);
        mDrawerListView.addHeaderView(headerLayout);
    }

    private void initPeekDrawer() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View headerView = inflater.inflate(R.layout.condensed_drawer_list_header, mPeekDrawerLayout, false);
        addProfilePicture((ImageView) headerView.findViewById(R.id.drawer_list_header_section_image));
        mPeekDrawerLayout.addView(headerView);
        View cViewToAdd;
        for(int i = 0; i < mDrawerItemModels.size(); i++) {
            if(mDrawerItemModels.get(i).getItemType() != DrawerItemModel.ItemType.MAIN_ITEM)
                continue;

            cViewToAdd = inflater.inflate(R.layout.condensed_drawer_list_item, mPeekDrawerLayout, false);
            cViewToAdd.setTag(i);
            ImageView imageView = (ImageView) cViewToAdd.findViewById(R.id.drawer_list_item_section_image);
            imageView.setImageResource(mDrawerItemModels.get(i).getItemImageResId());
            cViewToAdd.setOnClickListener(peekDrawerViewItemClick);
            mPeekDrawerLayout.addView(cViewToAdd);
        }
    }

    private void addProfilePicture(ImageView imageView) {
        Bitmap profileBitmap = DeviceProfileInformation.getInstance().getDeviceProfileImage(getActivity());
        imageView.setImageBitmap(CircularImage.getCircularImage(profileBitmap));
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void setUpForTablets(int fragmentId, CrossFadeSlidingPanelLayout crossFadeSlidingPanelLayout) {
        crossFadeSlidingPanelLayout.setShadowResourceLeft(R.drawable.drawer_shadow_right);
        crossFadeSlidingPanelLayout.setShadowResourceRight(R.drawable.drawer_shadow_right);
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        crossFadeSlidingPanelLayout.setSliderFadeColor(Color.TRANSPARENT);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarSlidingPanelToggle(getActivity(), R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }
        };

        ((ActionBarSlidingPanelToggle) mDrawerToggle).setDrawerLayout(crossFadeSlidingPanelLayout);

        mDrawerToggle.syncState();//TODO: inject CrossFadeSliding panel to adapter
        crossFadeSlidingPanelLayout.setSliderListener(mSlidingListener);
        getListViewVisibleItems();
        mDrawerListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                getListViewVisibleItems();
            }
        });
        //mAdapter.setCrossFadeSlidingListener(crossFadeSlidingPanelLayout);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
        if(mPeekDrawerLayout != null) {
            selectPeekItem(position);
        }
    }

    private void selectPeekItem(int position) {
        //TODO: implement
        for(int i = 1; i < mPeekDrawerLayout.getChildCount(); i++) {
            mPeekDrawerLayout.getChildAt(i).setBackgroundColor(
                    getResources().getColor(android.R.color.transparent));
        }

        mPeekDrawerLayout.getChildAt(position)
                .setBackgroundColor(getResources().getColor(R.color.menu_selected_item_color));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        /*if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }*/
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
