package com.vicmns.demogmaillikedrawer.lib;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vicmns on 2/12/2015.
 */
public class CrossFadeSlidingPanelLayout extends SlidingPaneLayout {
    private static final String TAG = "CrossFadeSPLayout";
    private View mFullView, mPeekView;
    private boolean mIsPaneOpen;
    private float mPreviousSlideOffset = 0;
    private boolean mUserStillTouching;
    private CrossFadeSlidingPanelListener mSliderListener;

    private SimplePanelSlideListener crossFadeListener
            = new SimplePanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            super.onPanelSlide(panel, slideOffset);
            boolean isPanelOpening = false;

            if (mPeekView == null || mFullView == null) {
                return;
            }

            if(slideOffset - mPreviousSlideOffset > 0) {
                //Opening drawer
                mFullView.setVisibility(VISIBLE);
                mPeekView.setVisibility(GONE);
                mPeekView.setAlpha(0);
                mFullView.setAlpha(1);
                isPanelOpening = true;
            } else if(slideOffset == 0 && !mUserStillTouching) {
                //Closing drawer
                mFullView.setVisibility(GONE);
                mPeekView.setVisibility(VISIBLE);
                mPeekView.setAlpha(1);
                mFullView.setAlpha(0);
                isPanelOpening = false;
            }

            if(mSliderListener != null) {
                mSliderListener.onPanelSlide(panel, slideOffset, isPanelOpening);
            }
            mPreviousSlideOffset = slideOffset;
        }
    };

    public CrossFadeSlidingPanelLayout(Context context) {
        super(context);
    }

    public CrossFadeSlidingPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrossFadeSlidingPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSliderListener(CrossFadeSlidingPanelListener listener) {
        mSliderListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() < 1) {
            return;
        }

        View panel = getChildAt(0);
        if (!(panel instanceof ViewGroup)) {
            return;
        }

        ViewGroup viewGroup = (ViewGroup) panel;
        /*if (((ViewGroup) panel).getChildAt(0) instanceof ViewGroup) {
            viewGroup = (ViewGroup) ((ViewGroup) panel).getChildAt(0);
        } else {
            viewGroup = (ViewGroup) panel;
        }
        if (viewGroup.getChildCount() != 2) {
            return;
        }*/
        mPeekView = viewGroup.getChildAt(0);
        mFullView = viewGroup.getChildAt(1);
        closePanelLayout();

        super.setPanelSlideListener(crossFadeListener);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        ss.isPaneOpen = isOpen();

        return ss;
    }

    @Override
    public boolean isOpen() {
        return mIsPaneOpen = super.isOpen();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mIsPaneOpen = ss.isPaneOpen;
        refreshPanelVisibility();
    }

    private void refreshPanelVisibility() {
        if(mIsPaneOpen) {
            openPanelLayout();
        } else {
            closePanelLayout();
        }

    }

    private void openPanelLayout() {
        if (mPeekView == null || mFullView == null) {
            return;
        }

        mPeekView.setVisibility(GONE);
        mPeekView.setAlpha(0);
        mFullView.setVisibility(VISIBLE);
        mFullView.setAlpha(1);
    }

    private void closePanelLayout() {
        if (mPeekView == null || mFullView == null) {
            return;
        }

        mPeekView.setVisibility(VISIBLE);
        mPeekView.setAlpha(1);
        mFullView.setVisibility(GONE);
        mFullView.setAlpha(0);
    }

    static class SavedState extends BaseSavedState {
        boolean isPaneOpen;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.isPaneOpen = in.readByte() != 0x00;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (isPaneOpen ? 0x01 : 0x00));
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_UP:
                mUserStillTouching = false;
                if(mPreviousSlideOffset == 0) {
                    closePanelLayout();

                }
                break;
            default:
                mUserStillTouching = true;
        }

        return super.onTouchEvent(ev);
    }

    public interface CrossFadeSlidingPanelListener {
        void onPanelSlide(View panel, float slideOffset, boolean isOpening);
    }
}
