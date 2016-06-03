package com.example.hexi.canvastest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.hexi.canvastest.R;

/**
 * Created by hexi on 16/6/1.
 */
public class CheckView extends ImageView implements View.OnClickListener {

    OnCheckStatusChangedListener onCheckStatusChangedListener;
    private Drawable checkedImage;
    private Drawable uncheckedImage;
    private boolean checked;

    public void setOnCheckStatusChangedListener(OnCheckStatusChangedListener onCheckStatusChangedListener) {
        this.onCheckStatusChangedListener = onCheckStatusChangedListener;
    }

    public CheckView(Context context) {
        this(context, null);
    }

    public CheckView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initStyle(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        if (checkedImage == null) {
            checkedImage = getResources().getDrawable(R.drawable.checkbox_checked);
        }
        if (uncheckedImage == null) {
            uncheckedImage = getResources().getDrawable(R.drawable.checkbox_unchecked);
        }
        setOnClickListener(this);
        update();
    }

    private void update() {
        if (checked) {
            setImageDrawable(checkedImage);
        } else {
            setImageDrawable(uncheckedImage);
        }
    }

    private void initStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckView, defStyleAttr, 0);
        checked = typedArray.getBoolean(R.styleable.CheckView_checked, false);
        checkedImage = typedArray.getDrawable(R.styleable.CheckView_checkedImage);
        uncheckedImage = typedArray.getDrawable(R.styleable.CheckView_uncheckedImage);

        typedArray.recycle();
    }

    @Override
    public void onClick(View v) {
        this.checked = !this.checked;
        update();
        if (onCheckStatusChangedListener != null) {
            onCheckStatusChangedListener.onCheckStatusChanged(checked);
        }
    }
}
