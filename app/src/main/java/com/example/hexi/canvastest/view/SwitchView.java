package com.example.hexi.canvastest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hexi.canvastest.R;


/**
 * Created by chengxin on 12/15/15.
 */
public class SwitchView extends LinearLayout {

    public interface OnActionSelectedListener {
        void onSelectAction(int action);
    }

    public static final int ACTION_LEFT = 0;
    public static final int ACTION_RIGHT = 1;

    private String textLeft;
    private String textRight;
    private boolean leftChecked;
    private int backgroundColor;

    private LeftSwitchItem leftAction;
    private RightSwitchItem rightAction;
    private TextView currentAction;
    private OnActionSelectedListener listener;

    public void setActionSelectedListener(OnActionSelectedListener listener) {
        this.listener = listener;
    }

    public SwitchView(Context context) {
        this(context, null);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.widget_switch, this, true);

        initStyle(context, attrs, defStyleAttr);

        initView();


    }

    private void initStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchView, defStyleAttr, 0);
        textLeft = typedArray.getString(R.styleable.SwitchView_textLeft);
        textRight = typedArray.getString(R.styleable.SwitchView_textRight);
        leftChecked = typedArray.getBoolean(R.styleable.SwitchView_leftChecked, true);
        backgroundColor = typedArray.getColor(R.styleable.SwitchView_backgroundColor,
                context.getResources().getColor(R.color.red));

        typedArray.recycle();
    }

    private void initView() {
        leftAction = (LeftSwitchItem) findViewById(R.id.tv_left_action);
        if (!TextUtils.isEmpty(textLeft)) {
            leftAction.setText(textLeft);
        }
        leftAction.setTag(ACTION_LEFT);

        rightAction = (RightSwitchItem) findViewById(R.id.tv_right_action);
        if (!TextUtils.isEmpty(textRight)) {
            rightAction.setText(textRight);
        }
        rightAction.setTag(ACTION_RIGHT);

        currentAction = leftAction;
        if (!leftChecked) {
            currentAction = rightAction;
        }
        currentAction.setSelected(true);

        leftAction.setOnClickListener(clickListener);
        rightAction.setOnClickListener(clickListener);

        leftAction.setSelectedBgColor(backgroundColor);
        rightAction.setSelectedBgColor(backgroundColor);

        setSelected(currentAction);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!(view instanceof TextView)) {
                return;
            }
            if (view == currentAction) {
                return;
            }
            setUnSelected(currentAction);

            currentAction = (TextView) view;
            setSelected(currentAction);
            if (listener != null) {
                listener.onSelectAction((int)currentAction.getTag());
            }
        }
    };

    private void setSelected(TextView view) {
        view.setSelected(true);
    }

    private void setUnSelected(TextView view) {
        view.setSelected(false);
    }

    public int selectedOn() {
        return (int)currentAction.getTag();
    }

    private static final String STATE_PARCELABLE = "parcelable";
    private static final String STATE_LEFT_TEXT = "leftText";
    private static final String STATE_RIGHT_TEXT = "rightText";
    private static final String STATE_SELECTED_ON = "selectedOn";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        Parcelable parcelable = super.onSaveInstanceState();
        bundle.putParcelable(STATE_PARCELABLE, parcelable);
        bundle.putString(STATE_LEFT_TEXT, leftAction.getText().toString());
        bundle.putString(STATE_RIGHT_TEXT, rightAction.getText().toString());
        int action = (int) currentAction.getTag();
        bundle.putInt(STATE_SELECTED_ON, action);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(STATE_PARCELABLE));
        leftAction.setText(bundle.getString(STATE_LEFT_TEXT));
        rightAction.setText(bundle.getString(STATE_RIGHT_TEXT));
        int selectedOn = bundle.getInt(STATE_SELECTED_ON);
        if (selectedOn == ACTION_LEFT) {
            leftAction.performClick();
        } else {
            rightAction.performClick();
        }
    }
}
