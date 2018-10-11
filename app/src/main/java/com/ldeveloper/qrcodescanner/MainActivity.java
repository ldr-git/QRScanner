package com.ldeveloper.qrcodescanner;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ldeveloper.qrcodescanner.helper.IntentHelper;
import com.ldeveloper.qrcodescanner.helper.PermissionHelper;
import com.ldeveloper.qrcodescanner.helper.ProgressHelper;
import com.ldeveloper.qrcodescanner.helper.URLHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.HttpUrl;

import static com.ldeveloper.qrcodescanner.helper.RequestType.PERMISSION_REQUEST;

public class MainActivity extends BaseActivity {

    private static final String TAG = "SCANNER";

    private PermissionHelper permissionHelper;

    @BindView(R.id.scannerView)
    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        PermissionHelper.Builder builder = new PermissionHelper.Builder(this)
                .addPermissions(Manifest.permission.CAMERA)
                .requestCode(PERMISSION_REQUEST)
                .setPermissionListener((PermissionHelper.RequestPermissionRationaleListener) permissionsNeedRationale -> {
                    Log.d(TAG, "initiatePermissionHelper: " + permissionsNeedRationale);
                    showDeniedPermissionDialog(this, permissionsNeedRationale);
                })
                .setPermissionListener(new PermissionHelper.RequestPermissionListener() {
                    @Override
                    public void permissionsGranted() {
                        scannerView.setResultHandler(handler); // Register ourselves as a handler for scan results.
                        scannerView.startCamera(); // Start camera on resume
                    }

                    @Override
                    public void permissionsDenied() {
                        permissionHelper.check();
                    }
                });
        permissionHelper = builder.build();

    }

    private ZXingScannerView.ResultHandler handler = result -> {
        ProgressHelper.show(this);
        String value = URLHelper.isValidUrl(result.getText()) ? String.valueOf(HttpUrl.parse(result.getText())) : result.getText();
        new IntentHelper.Builder(this)
                .putExtra("text", value)
                .toClass(ResultActivity.class)
                .show();
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionHelper.check();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

}
