package com.hexi.activitydemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hexi on 2017/7/30.
 */

public class DialogUtil {

    public interface onDialogDismiss {
        void onDismiss();
    }

    public static void showDialog(Activity activity,
                                  final onDialogDismiss onDialogDismiss) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setPositiveButton("立即查看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (onDialogDismiss != null) {
                            onDialogDismiss.onDismiss();
                        }
                    }
                })
                .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (onDialogDismiss != null) {
                            onDialogDismiss.onDismiss();
                        }
                    }
                })
                .create();

        View title = View.inflate(activity, R.layout.dialog_title, null);
        if (title != null) {
            TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
            titleText.setText("消息");
            dialog.setCustomTitle(title);
        }
        dialog.setMessage("这是一条消息提醒");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onDialogDismiss != null) {
                    onDialogDismiss.onDismiss();
                }
            }
        });
    }
}
