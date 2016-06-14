package com.baidao.ytxplayer.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.pili.pldroid.player.IMediaController;

import java.lang.reflect.Method;

/**
 * You can write a custom MediaController instead of this class
 * A MediaController widget must implement all the interface defined by com.pili.pldroid.player.IMediaController
 */
public class MediaController extends FrameLayout implements IMediaController {
    private static final String TAG = "PLMediaController";

    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private MediaPlayerControl mPlayer;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private boolean mShowing;
    private boolean mFromXml = false;
    private ImageButton mPauseButton;
    private ImageButton mToLandscapeButton;
    private ImageButton mToPortraitButton;
    private OnShownListener mShownListener;
    private OnHiddenListener mHiddenListener;
    private OnOrientationChangeListener mOrientationChangeListener;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
            }
        }
    };
    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;
        initController(context);
    }

    public MediaController(Context context) {
        super(context);
        if (!mFromXml && initController(context))
            initFloatingWindow();
    }

    private boolean initController(Context context) {
        mContext = context;
        return true;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", new Class[]{int.class});
                setWindowLayoutType.invoke(mWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * Set the view that acts as the anchor for the control view. This can for
     * example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view) {
        if (mAnchor != null) {
            return;
        }
        mAnchor = view;
        if (!mFromXml) {
            removeAllViews();
            mRoot = makeControllerView();
            mRoot.measure(MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED);
            mWindow.setContentView(mRoot);

            WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            int usableWidth = metrics.widthPixels;
            mWindow.setWidth(usableWidth);
            mWindow.setHeight(LayoutParams.WRAP_CONTENT);

        }
        initControllerView(mRoot);
    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The controller view.
     */
    protected View makeControllerView() {
        return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getResources().getIdentifier("mediacontroller", "layout", mContext.getPackageName()), this);
    }

    private void initControllerView(View v) {
        mPauseButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_play_pause", "id", mContext.getPackageName()));
        mToLandscapeButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_to_landscape", "id", mContext.getPackageName()));
        mToPortraitButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_to_portrait", "id", mContext.getPackageName()));
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mToLandscapeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrientationChangeListener != null) {
                    mOrientationChangeListener.toLandscape();
                }
            }
        });

        mToPortraitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrientationChangeListener != null) {
                    mOrientationChangeListener.toPortrait();
                }
            }
        });

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            showToPortraitButton();
        } else {
            showToLandButton();
        }

    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * <p>
     * Change the animation style resource for this controller.
     * </p>
     * <p/>
     * <p>
     * If the controller is showing, calling this method will take effect only the
     * next time the controller is shown.
     * </p>
     *
     * @param animationStyle animation style to use when the controller appears
     *                       and disappears. Set to -1 for the default animation, 0 for no animation, or
     *                       a resource identifier for an explicit animation.
     */
    public void setAnimationStyle(int animationStyle) {
        mAnimStyle = animationStyle;
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show the controller
     *                until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
            if (mPauseButton != null)
                mPauseButton.requestFocus();

            if (mFromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];

                mAnchor.getLocationOnScreen(location);
                int popupHeight = mWindow.getContentView().getMeasuredHeight();
                Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight() - popupHeight);

                mWindow.setAnimationStyle(mAnimStyle);
//                setWindowLayoutType();
                mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, anchorRect.left, anchorRect.bottom);
            }
            mShowing = true;
            if (mShownListener != null)
                mShownListener.onShown();
        }
        updatePausePlay();

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void hide() {
        if (mAnchor == null)
            return;

        if (mShowing) {
            try {
                if (mFromXml)
                    setVisibility(View.GONE);
                else
                    mWindow.dismiss();
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }
    }

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    public void setOrientationChangeListener(OnOrientationChangeListener orientationChangeListener) {
        this.mOrientationChangeListener = orientationChangeListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    public void showToLandButton() {
        mToLandscapeButton.setVisibility(VISIBLE);
        mToPortraitButton.setVisibility(GONE);
    }

    public void showToPortraitButton() {
        mToLandscapeButton.setVisibility(GONE);
        mToPortraitButton.setVisibility(VISIBLE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
            doPauseResume();
            show(sDefaultTimeout);
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            hide();
            return true;
        } else {
            show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
    }

    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null)
            return;

        if (mPlayer.isPlaying())
            mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_pause", "drawable", mContext.getPackageName()));
        else
            mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", mContext.getPackageName()));
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying())
            mPlayer.pause();
        else
            mPlayer.start();
        updatePausePlay();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null)
            mPauseButton.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public interface OnShownListener {
        public void onShown();
    }

    public interface OnHiddenListener {
        public void onHidden();
    }

    public interface OnOrientationChangeListener {
        void toLandscape();

        void toPortrait();
    }
}
