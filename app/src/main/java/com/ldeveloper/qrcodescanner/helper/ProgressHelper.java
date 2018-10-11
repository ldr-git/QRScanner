package com.ldeveloper.qrcodescanner.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.ldeveloper.qrcodescanner.R;

import java.lang.ref.WeakReference;

public class ProgressHelper {

    private static Dialog dialog;

    public static void show(View view) {
        show(view.getContext());
    }

    public static void show(Activity activity) {
        show((Context) activity);
    }

    public static void show(Fragment fragment) {
        show((Context) fragment.getActivity());
    }

    public static void show(Context host) {
        WeakReference<Context> context = new WeakReference<>(host);
        if (dialog == null) {
            dialog = new Dialog(context.get());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(LayoutInflater.from(context.get()).inflate(R.layout.layout_progress_dialog, null, false));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
        try {
            if (!dialog.isShowing())
                dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hide() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}