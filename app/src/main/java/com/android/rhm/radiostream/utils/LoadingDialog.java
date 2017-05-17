package com.android.rhm.radiostream.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import com.android.rhm.radiostream.R;
import com.android.rhm.radiostream.activity.MainActivity;

/**
 * Created by soklundy on 5/6/2017.
 */

public class LoadingDialog {
    private Dialog dialog;
    private Context mContext;

    public LoadingDialog (Context context) {
        mContext = context;
    }

    public void loading () {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.alert_progress);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void closeLoad() {
        dialog.dismiss();
    }

    public void alertMessage(String s) {
        new AlertDialog.Builder(mContext)
                .setMessage(s)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
