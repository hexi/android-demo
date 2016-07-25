package com.example.hexi.canvastest.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.util.DisplaySizeUtil;


/**
 * Created by weijia.jin on 16/5/6.
 */
public class CustomKeyboard extends PopupWindow implements View.OnClickListener {
    private static final String TAG = "CustomKeyboard";
    public static final int TYPE_PRICE = 0;
    public static final int TYPE_AMOUNT = 1;
    public static final int TYPE_TRANSFER = 2;

    public static final int AMOUNT_FULL = 124;
    public static final int AMOUNT_HALF = AMOUNT_FULL + 1;
    public static final int AMOUNT_ONETHIRD = AMOUNT_HALF + 1;
    public static final int AMOUNT_TWOTHIRD = AMOUNT_ONETHIRD + 1;
    public static final int AMOUNT_ONEFOUTH = AMOUNT_TWOTHIRD + 1;
    private int type = TYPE_PRICE;
    private EditText edit;
    private Context context;
    private KeyBoardListener listener;
    private Button key_1, key_2, key_3, key_4, key_5, key_6, key_7, key_8, key_9, key_0, key_point;
    private LinearLayout key_del, key_dismiss;
    private Button key_full, key_half, key_oneThird, key_twoThird, key_oneFouth, key_buy;

    public void setEdit(EditText edit) {
        this.edit = edit;
    }

    public interface KeyBoardListener {
        void onAmountChange(int index);

        void onBuyClick();
    }

    public CustomKeyboard(Context context) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, (int) DisplaySizeUtil.dp2px(context.getResources(), 200));
        this.context = context;
        this.setContentView(LayoutInflater.from(context).inflate(R.layout.price_keyboard, null));
        getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    CustomKeyboard.this.dismiss();
                }
                return false;
            }
        });
        initViews();
        //setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(true);
        this.setFocusable(false);
    }

    private void initViews() {
        key_1 = (Button) getContentView().findViewById(R.id.key_1);
        key_2 = (Button) getContentView().findViewById(R.id.key_2);
        key_3 = (Button) getContentView().findViewById(R.id.key_3);
        key_4 = (Button) getContentView().findViewById(R.id.key_4);
        key_5 = (Button) getContentView().findViewById(R.id.key_5);
        key_6 = (Button) getContentView().findViewById(R.id.key_6);
        key_7 = (Button) getContentView().findViewById(R.id.key_7);
        key_8 = (Button) getContentView().findViewById(R.id.key_8);
        key_9 = (Button) getContentView().findViewById(R.id.key_9);
        key_0 = (Button) getContentView().findViewById(R.id.key_0);
        key_point = (Button) getContentView().findViewById(R.id.key_point);
        key_del = (LinearLayout) getContentView().findViewById(R.id.key_del);
        key_dismiss = (LinearLayout) getContentView().findViewById(R.id.key_dismiss);
        key_buy = (Button) getContentView().findViewById(R.id.key_buy);
        if (type == TYPE_AMOUNT) {
            key_full = (Button) getContentView().findViewById(R.id.key_full);
            key_half = (Button) getContentView().findViewById(R.id.key_half);
            key_oneThird = (Button) getContentView().findViewById(R.id.key_oneThird);
            key_twoThird = (Button) getContentView().findViewById(R.id.key_twoThird);
            key_oneFouth = (Button) getContentView().findViewById(R.id.key_oneFouth);

            key_full.setOnClickListener(this);
            key_half.setOnClickListener(this);
            key_oneThird.setOnClickListener(this);
            key_twoThird.setOnClickListener(this);
            key_oneFouth.setOnClickListener(this);
        }
        key_0.setOnClickListener(this);
        key_1.setOnClickListener(this);
        key_2.setOnClickListener(this);
        key_3.setOnClickListener(this);
        key_4.setOnClickListener(this);
        key_5.setOnClickListener(this);
        key_6.setOnClickListener(this);
        key_7.setOnClickListener(this);
        key_8.setOnClickListener(this);
        key_9.setOnClickListener(this);
        key_point.setOnClickListener(this);
        key_del.setOnClickListener(this);
        key_buy.setOnClickListener(this);
        key_dismiss.setOnClickListener(this);
    }

    public void setOnKeyboardListener(KeyBoardListener listener) {
        this.listener = listener;
    }

    public CustomKeyboard(Context context, int type) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, (int) DisplaySizeUtil.dp2px(context.getResources(), 200));
        this.type = type;
        this.context = context;
        if (this.type == TYPE_PRICE) {
            this.setContentView(LayoutInflater.from(context).inflate(R.layout.price_keyboard, null));
        } else if (this.type == TYPE_AMOUNT) {
            this.setContentView(LayoutInflater.from(context).inflate(R.layout.amount_keyboard, null));
        } else {

        }
        initViews();
        setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(true);
        this.setFocusable(true);
    }

    public void showAtLocation(View parent) {
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        if (!hasBackKey || !hasHomeKey) {
            getContentView().setPadding(0, 0, 0, (int) DisplaySizeUtil.dp2px(context.getResources(), 48f));
        }

        this.setAnimationStyle(R.style.animation_prelogin);
        this.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (edit == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.key_1:
                edit.getText().insert(edit.getSelectionStart(), "1");
                break;
            case R.id.key_2:
                edit.getText().insert(edit.getSelectionStart(), "2");
                break;
            case R.id.key_3:
                edit.getText().insert(edit.getSelectionStart(), "3");
                break;
            case R.id.key_4:
                edit.getText().insert(edit.getSelectionStart(), "4");
                break;
            case R.id.key_5:
                edit.getText().insert(edit.getSelectionStart(), "5");
                break;
            case R.id.key_6:
                edit.getText().insert(edit.getSelectionStart(), "6");
                break;
            case R.id.key_7:
                edit.getText().insert(edit.getSelectionStart(), "7");
                break;
            case R.id.key_8:
                edit.getText().insert(edit.getSelectionStart(), "8");
                break;
            case R.id.key_9:
                edit.getText().insert(edit.getSelectionStart(), "9");
                break;
            case R.id.key_0:
                edit.getText().insert(edit.getSelectionStart(), "0");
                break;
            case R.id.key_point:
                if (type == TYPE_PRICE) {
                    edit.getText().insert(edit.getSelectionStart(), ".");
                } else {
                    edit.getText().insert(edit.getSelectionStart(), "000");
                }

                break;
            case R.id.key_del:
                if (edit.getSelectionStart() > 0) {
                    edit.getText().delete(edit.getSelectionStart() - 1, edit.getSelectionStart());
                }
                break;
            case R.id.key_dismiss:
                this.dismiss();
                break;
            case R.id.key_full:
                if (listener != null) {
                    listener.onAmountChange(AMOUNT_FULL);
                }
                break;
            case R.id.key_half:
                if (listener != null) {
                    listener.onAmountChange(AMOUNT_HALF);
                }
                break;
            case R.id.key_oneFouth:
                if (listener != null) {
                    listener.onAmountChange(AMOUNT_ONEFOUTH);
                }
                break;
            case R.id.key_oneThird:
                if (listener != null) {
                    listener.onAmountChange(AMOUNT_ONETHIRD);
                }
                break;
            case R.id.key_twoThird:
                if (listener != null) {
                    listener.onAmountChange(AMOUNT_TWOTHIRD);
                }
                break;
            case R.id.key_buy:
                this.dismiss();
                if (listener != null) {
                    listener.onBuyClick();
                }
                break;
        }
    }
}
