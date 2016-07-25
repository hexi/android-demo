package com.example.hexi.canvastest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.util.BigDecimalUtil;
import com.example.hexi.canvastest.util.ImageCache;
import com.example.hexi.canvastest.util.OnValueChangedObserver;
import com.example.hexi.canvastest.view.listener.OnValueChangedListener;
import com.example.hexi.canvastest.view.listener.OnValueChangedObservable;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by rjhy on 15-12-14.
 */
public class NumberSettingView extends LinearLayout implements View.OnFocusChangeListener, OnValueChangedObservable {

    @InjectView(R.id.ll_number_setting_container)
    LinearLayout containerView;

    @InjectView(R.id.et_input)
    EditText editTextView;
    @InjectView(R.id.minus)
    View minusView;
    @InjectView(R.id.add)
    View addView;
    @InjectView(R.id.iv_minus_icon)
    ImageView minusIcon;
    @InjectView((R.id.iv_plus_icon))
    ImageView plusIcon;

    private static final double DEFAULT_UNIT = 1;
    private static final int DEFAULT_SCALE = 2;
    private double unit = DEFAULT_UNIT;
    private double maxValue = Double.MAX_VALUE;
    private double minValue = 0;
    private int scale;
    private CustomKeyboard keyboard;

    public void setScale(int scale) {
        this.scale = scale;
        this.unit = Math.pow(1, scale) / Math.pow(10, scale);
    }

    OnValueChangedObserver onValueChangedObserver = new OnValueChangedObserver();

    @Override
    public void addOnValueChangedListener(OnValueChangedListener listener) {
        onValueChangedObserver.addOnValueChangedListener(listener);
    }

    @Override
    public void removeOnValueChangedListener(OnValueChangedListener listener) {
        onValueChangedObserver.removeOnValueChangedListener(listener);
    }

    public NumberSettingView(Context context) {
        this(context, null);
    }

    public NumberSettingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.widget_number_setting, this, true);
        ButterKnife.inject(this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberSettingView, defStyleAttr, 0);
        boolean enabled = typedArray.getBoolean(R.styleable.NumberSettingView_enabled, true);
        int inputType = typedArray.getInt(R.styleable.NumberSettingView_inputType, 1);
        String hint = typedArray.getString(R.styleable.NumberSettingView_hint);
        typedArray.recycle();
        if (inputType == 1) {
            editTextView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
            setScale(0);
            keyboard = new CustomKeyboard(getContext(), CustomKeyboard.TYPE_AMOUNT);
        } else {
            setScale(DEFAULT_SCALE);
            editTextView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            keyboard = new CustomKeyboard(getContext(), CustomKeyboard.TYPE_PRICE);
        }
        keyboard.setEdit(editTextView);
        if (!TextUtils.isEmpty(hint)) {
            editTextView.setHint(hint);
        }
        editTextView.setOnFocusChangeListener(this);

        editTextView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!editTextView.isFocused()) {
                    editTextView.setFocusable(true);
                }
                if (!keyboard.isShowing()) {
                    keyboard.showAtLocation(editTextView);
                }
                editTextView.requestFocusFromTouch();
                return false;
            }
        });
        editTextView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
        keyboard.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String numberStr = getTextValue();
                double value = parseValue(numberStr);
                value = fixValue(value);

                numberStr = format(value);
                editTextView.setText(numberStr);
                editTextView.setSelection(numberStr.length());

                onValueChanged(getValue());
            }
        });
        editTextView.setLongClickable(false);
        editTextView.setFocusable(false);
        hideSoftInputMethod(editTextView);
        setEnabled(enabled);
    }

    public void setKeyboardListener(CustomKeyboard.KeyBoardListener listener) {
        if (keyboard != null) {
            keyboard.setOnKeyboardListener(listener);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        containerView.setEnabled(enabled);
        editTextView.setEnabled(enabled);
        minusView.setEnabled(enabled);
        addView.setEnabled(enabled);

        if (enabled) {
            minusIcon.setImageDrawable(ImageCache.getImage(getContext(), R.drawable.ic_minus_enable));
            plusIcon.setImageDrawable(ImageCache.getImage(getContext(), R.drawable.ic_plus_enable));
        } else {
            minusIcon.setImageDrawable(ImageCache.getImage(getContext(), R.drawable.ic_minus_disable));
            plusIcon.setImageDrawable(ImageCache.getImage(getContext(), R.drawable.ic_plus_disable));
        }
    }

    public void hideSoftInputMethod(EditText ed) {

//        ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        String methodName = null;
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";  //
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            //最低级最不济的方式，这个方式会把光标给屏蔽
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setUnit(double unit) {
        this.unit = unit;
    }

    public void setRangeLimit(double min, double max) {
        double lMin = min;
        double lMax = max;
        if (min > max) {
            lMin = max;
            lMax = min;
        }
        this.minValue = lMin;
        this.maxValue = lMax;
    }

    @Override
    public double getValue() {
        return Double.parseDouble(getTextValue());
    }

    private String getTextValue() {
        String value = editTextView.getText().toString();
        if (TextUtils.isEmpty(value) || value.equals(".")) {
            return "0";
        } else {
            return value;
        }
    }
    
    @OnClick({R.id.minus, R.id.add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.minus:
                minus();
                break;
            case R.id.add:
                add();
                break;
        }
    }

    public void hideOperateButton() {
        minusView.setVisibility(GONE);
        addView.setVisibility(GONE);
    }

    private void add() {
        String value = getTextValue();
        BigDecimal bigDecimal1 = new BigDecimal(value);
        BigDecimal bigDecimal2 = new BigDecimal(unit);
        BigDecimal bigDecimal = bigDecimal1.add(bigDecimal2);
        double newValue = bigDecimal.doubleValue();
        newValue = fixValue(newValue);

        editTextView.setText(format(newValue));
        editTextView.setSelection(editTextView.getText().toString().length());

        onValueChanged(getValue());
    }

    private void minus() {
        String value = getTextValue();
        BigDecimal bigDecimal1 = new BigDecimal(value);
        BigDecimal bigDecimal2 = new BigDecimal(unit);
        BigDecimal bigDecimal = bigDecimal1.subtract(bigDecimal2);
        double newValue = bigDecimal.doubleValue();
        newValue = fixValue(newValue);

        editTextView.setText(format(newValue));
        editTextView.setSelection(editTextView.getText().toString().length());

        onValueChanged(getValue());
    }

    private final static String STATE_SCALE = "state_scale";
    private final static String STATE_PARCELABLE = "state_parcelable";
    private final static String STATE_MAXVALUE = "state_maxValue";
    private final static String STATE_MINVALUE = "state_minValue";
    private final static String STATE_UNIT = "state_unit";
    private final static String STATE_VALUE = "state_value";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        Parcelable parcelable = super.onSaveInstanceState();
        bundle.putParcelable(STATE_PARCELABLE, parcelable);
        bundle.putDouble(STATE_MAXVALUE, maxValue);
        bundle.putDouble(STATE_MINVALUE, minValue);
        bundle.putDouble(STATE_UNIT, unit);
        bundle.putInt(STATE_SCALE, scale);
        bundle.putString(STATE_VALUE, getTextValue());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(STATE_PARCELABLE));
        maxValue = bundle.getDouble(STATE_MAXVALUE);
        minValue = bundle.getDouble(STATE_MINVALUE);
        unit = bundle.getDouble(STATE_UNIT);
        scale = bundle.getInt(STATE_SCALE);
        String value = bundle.getString(STATE_VALUE);
        if (!TextUtils.isEmpty(value) && !value.equals("0") && !value.equals("0.0")) {
            editTextView.setText(format(value));
        } else {
            editTextView.setText("");
        }
    }

    public void onValueChanged(double value) {
        if (onValueChangedObserver == null) {
            return;
        }
        onValueChangedObserver.notifyValueChanged(value);
    }

    private String format(double value) {
        return BigDecimalUtil.format(value, scale);
    }

//    @Override
//    public void clearFocus() {
//        if (editTextView.isFocused()) {
//            this.editTextView.setFocusable(false);
//        }
//    }

    private String format(String value) {
        double newValue = Double.parseDouble(value);
        return BigDecimalUtil.format(newValue, scale);
    }

    public void setValue(String value) {
        this.editTextView.setText(value);
        onValueChanged(getValue());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            final InputMethodManager mgr = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(this.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            String numberStr = getTextValue();
            double value = parseValue(numberStr);
            value = fixValue(value);

            numberStr = format(value);
            editTextView.setText(numberStr);
            editTextView.setSelection(numberStr.length());

            onValueChanged(getValue());
        }
    }

    private double fixValue(double value) {
        if (value > maxValue) {
            value = maxValue;
        } else if (value < minValue) {
            value = minValue;
        }
        return value;
    }

    private double parseValue(String numberStr) {
        try {
            return Double.parseDouble(numberStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * get the animation text end coordinate location
     * note : the animation number text is not match the textview height
     * @param text
     * @return
     */
    public Point getLocation(String text){
        Paint pFont = new Paint();
        pFont.setTextSize(editTextView.getTextSize());
        Rect rect = new Rect();
        pFont.getTextBounds(text, 0, text.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();
        int[] locations = new int[2];
        editTextView.getLocationOnScreen(locations);
        Point point = new Point();
        point.x = locations[0] + editTextView.getWidth()/2 - textWidth/2;
        point.y = locations[1] + textHeight/2;//FIXME
        return point;
    }

}
