package com.ldeveloper.qrcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public abstract class BaseActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private String permissionName;

    protected void showDeniedPermissionDialog(Activity context, String permissionName) {
        if (alertDialog == null) {
            this.permissionName = permissionName;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle("Permission Required")
                    .setMessage(String.format("Allow %s to access your %s by tapping Settings > Permissions > %s.", context.getResources().getString(R.string.app_name), permissionName, permissionName))
                    .setPositiveButton("SETTINGS", (dialogInterface, i) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    });
            alertDialog = builder.create();
            alertDialog.show();
        } else {
            if (this.permissionName.equalsIgnoreCase(permissionName)) {
                if (!alertDialog.isShowing())
                    alertDialog.show();
            } else {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                    alertDialog = null;
                    showDeniedPermissionDialog(context, permissionName);
                }
            }
        }
    }

    protected void toolbarShowBackWithTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
