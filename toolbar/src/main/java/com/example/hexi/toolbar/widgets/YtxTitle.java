package com.example.hexi.toolbar.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hexi.toolbar.R;


/**
 * Created by hexi on 15/12/2.
 */
public class YtxTitle extends RelativeLayout {
    private String leftActionText;
    private Drawable leftActionImage;
    private String rightActionText;
    private Drawable rightActionImage;
    private Drawable titleBackgroudDrawable;
    private String title;
    private String subTitle;

    private TextView leftActionTextView;
    private ImageView leftActionImageView;

    public TextView getRightActionTextView() {
        return rightActionTextView;
    }

    private TextView rightActionTextView;

    public ImageView getRightActionImageView() {
        return rightActionImageView;
    }

    private RelativeLayout titleRootView;
    private ImageView rightActionImageView;
    private TextView titleView;
    private TextView subTitleView;
    private ViewGroup leftAction;
    private ViewGroup rightAction;

    private ViewGroup rightActionContainer;

    /**
     * action listener
     */
    public static class OnActionClickListener {
        /**
         * left click method
         */
        public void onClickedLeftAction() {

        }

        /**
         * right click method
         */
        public void onClickedRightAction() {

        }
    }

    private OnActionClickListener listener = new OnActionClickListener() {
    };

    /**
     * set action listener
     *
     * @param listener listener
     */
    public void setOnActionListener(OnActionClickListener listener) {
        this.listener = listener;
    }

    public YtxTitle(Context context) {
        super(context);
        init(null, 0);
    }

    public YtxTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public YtxTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        // Load attributes
        initAttrs(attrs, defStyleAttr);
        initView();
        refreshUI();
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a =
                getContext().obtainStyledAttributes(attrs, R.styleable.YtxTitle, defStyleAttr, 0);
        try {
            leftActionText = a.getString(R.styleable.YtxTitle_leftActionText);
            leftActionImage = a.getDrawable(R.styleable.YtxTitle_leftActionImage);
            rightActionText = a.getString(R.styleable.YtxTitle_rightActionText);
            rightActionImage = a.getDrawable(R.styleable.YtxTitle_rightActionImage);
            titleBackgroudDrawable = a.getDrawable(R.styleable.YtxTitle_titleBackground);
            title = a.getString(R.styleable.YtxTitle_barTitle);
            subTitle = a.getString(R.styleable.YtxTitle_barSubTitle);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_ytx_title, this);
        this.titleRootView = (RelativeLayout) findViewById(R.id.rl_common_title);
        this.leftActionTextView = (TextView) findViewById(R.id.tv_left_action);
        this.leftActionImageView = (ImageView) findViewById(R.id.iv_left_action);
        this.titleView = (TextView) findViewById(R.id.tv_title);
        this.subTitleView = (TextView) findViewById(R.id.tv_sub_title);
        this.leftAction = (ViewGroup) findViewById(R.id.rl_left_action);
        this.rightActionContainer = (ViewGroup) findViewById(R.id.fl_right_action_container);
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        super.setBackgroundColor(color);
        this.titleRootView.setBackgroundColor(color);
    }

    /**
     * refresh ui
     */
    public void refreshUI() {
        refreshLeftContainer();

        if (titleBackgroudDrawable != null) {
            titleRootView.setBackgroundDrawable(titleBackgroudDrawable);
        }

        refreshTitleText();

        refreshRightContainer();
    }

    private void refreshTitleText() {
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }

        if (!TextUtils.isEmpty(subTitle)) {
            subTitleView.setText(subTitle);
            subTitleView.setVisibility(VISIBLE);
        } else {
            subTitleView.setVisibility(GONE);
        }
    }

    private void refreshLeftContainer() {
        boolean leftVisible = !TextUtils.isEmpty(leftActionText)
                || leftActionImage != null;

        if (!leftVisible) {
            leftAction.setVisibility(View.INVISIBLE);
            return;
        }
        leftAction.setVisibility(View.VISIBLE);
        leftAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickedLeftAction();
                }
            }
        });

        if (!TextUtils.isEmpty(leftActionText)) {
            leftActionTextView.setVisibility(View.VISIBLE);
            leftActionTextView.setText(leftActionText);
        } else {
            leftActionTextView.setVisibility(View.GONE);
        }

        if (leftActionImage != null) {
            leftActionImageView.setImageDrawable(leftActionImage);
            leftActionImageView.setVisibility(VISIBLE);
        } else {
            leftActionImageView.setVisibility(GONE);
        }
    }

    private void refreshRightContainer() {
        boolean rightVisible = !TextUtils.isEmpty(rightActionText)
                || rightActionImage != null;

        if (!rightVisible) {
            if (rightAction != null) {
                rightAction.setVisibility(View.INVISIBLE);
            }
            return;
        }

        initRightActionContainer();

        rightAction.setVisibility(View.VISIBLE);
        rightAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickedRightAction();
                }
            }
        });

        if (!TextUtils.isEmpty(rightActionText)) {
            rightActionTextView.setVisibility(View.VISIBLE);
            rightActionTextView.setText(rightActionText);
        } else {
            rightActionTextView.setVisibility(View.GONE);
        }

        if (rightActionImage != null) {
            rightActionImageView.setImageDrawable(rightActionImage);
            rightActionImageView.setVisibility(VISIBLE);
        } else {
            rightActionImageView.setVisibility(GONE);
        }
    }

    private void initRightActionContainer() {
        if (rightAction == null) {
            ViewStub viewStub = (ViewStub) rightActionContainer.findViewById(R.id.vs_ytx_title_right_container);
            rightAction = (ViewGroup) viewStub.inflate();
            rightActionTextView = (TextView) rightAction.findViewById(R.id.tv_right_action);
            rightActionImageView = (ImageView) rightAction.findViewById(R.id.iv_right_action);
        }
    }

    public void setLeftTextAction(OnClickListener listener) {
        leftActionTextView.setOnClickListener(listener);
    }

    public void setLeftTextVisiable(int visiable) {
        leftActionTextView.setVisibility(visiable);
    }

    /**
     * set sub title
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        refreshTitleText();
    }

    /**
     * set title
     */
    public void setTitle(String title) {
        this.title = title;
        refreshTitleText();
    }

    /**
     * set right action text
     */
    public void setRightActionText(String rightActionText) {
        this.rightActionText = rightActionText;
        refreshRightContainer();
    }

    /**
     * set right action text color
     */
    public void setRightActionTextColor(String color) {
        if (rightActionTextView != null) {
            rightActionTextView.setTextColor(Color.parseColor(color));
        }
    }

    /**
     * set left action text
     */
    public void setLeftActionText(String leftActionText) {
        this.leftActionText = leftActionText;
        refreshLeftContainer();
    }

    /**
     * set right action image
     */
    public void setRightActionImageView(Drawable rightActionImage) {
        this.rightActionImage = rightActionImage;
        refreshRightContainer();
    }

    /**
     * set left action image
     */
    public void setLeftActionImageView(Drawable leftActionImage) {
        this.leftActionImage = leftActionImage;
        refreshLeftContainer();
    }

}
