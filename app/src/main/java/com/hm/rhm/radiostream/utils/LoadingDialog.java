package com.hm.rhm.radiostream.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import com.android.rhm.radiostream.R;

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
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
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
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void alert(DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle(mContext.getResources().getString(R.string.can_load))
                .setMessage(mContext.getResources().getString(R.string.check_inter))
                .setPositiveButton(android.R.string.yes, listener)
                .show();
    }

    public void alert() {
        new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle(mContext.getResources().getString(R.string.can_load))
                .setMessage(mContext.getResources().getString(R.string.check_inter))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
