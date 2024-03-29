package com.ldeveloper.qrcodescanner.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionHelper {

    private static final String TAG = PermissionHelper.class.getSimpleName();

    private WeakReference<Activity> activityWeakReference;
    private WeakReference<Fragment> fragmentWeakReference;
    private PermisionHolder permisionHolder;
    private String[] permissions;
    private RequestPermissionRationaleListener requestPermissionRationaleListener;
    private RequestPermissionGrantedListener requestPermissionGrantedListener;
    private RequestPermissionDeniedListener requestPermissionDeniedListener;
    private RequestPermissionListener requestPermissionListener;
    private int requestCode;

    PermissionHelper(Builder builder) {

        this.activityWeakReference = builder.activityWeakReference;
        this.fragmentWeakReference = builder.fragmentWeakReference;
        this.permisionHolder = builder.permisionHolder;
        this.permissions = builder.permissions;
        this.requestPermissionRationaleListener = builder.requestPermissionRationaleListener;
        this.requestPermissionGrantedListener = builder.requestPermissionGrantedListener;
        this.requestPermissionDeniedListener = builder.requestPermissionDeniedListener;
        this.requestPermissionListener = builder.requestPermissionListener;
        this.requestCode = builder.requestCode;

    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                Log.d(TAG, "hasPermissions: " + permission);
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public interface RequestPermissionRationaleListener {
        void shouldShowRequestPermissionRationale(String permissionsNeedRationale);
    }

    public interface RequestPermissionGrantedListener {
        void hasPermission(List<String> permissionsGranted);
    }

    public interface RequestPermissionDeniedListener {
        void hasDeniedPermission(List<String> permissionsDenied);
    }

    public interface RequestPermissionListener {
        void permissionsGranted();

        void permissionsDenied();
    }

    private enum PermisionHolder {
        ACTIVITY,
        FRAGMENT
    }

    public static final class Builder {

        private WeakReference<Activity> activityWeakReference;
        private WeakReference<Fragment> fragmentWeakReference;
        private PermisionHolder permisionHolder;
        private String[] permissions;
        private RequestPermissionRationaleListener requestPermissionRationaleListener;
        private RequestPermissionGrantedListener requestPermissionGrantedListener;
        private RequestPermissionDeniedListener requestPermissionDeniedListener;
        private RequestPermissionListener requestPermissionListener;
        private int requestCode;

        public Builder(Activity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.permisionHolder = PermisionHolder.ACTIVITY;
        }

        public Builder(Fragment fragment) {
            this.fragmentWeakReference = new WeakReference<>(fragment);
            this.permisionHolder = PermisionHolder.FRAGMENT;
        }

        public Builder addPermissions(@NonNull String... permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder setPermissionListener(RequestPermissionRationaleListener requestPermissionRationaleListener) {
            this.requestPermissionRationaleListener = requestPermissionRationaleListener;
            return this;
        }

        public Builder setPermissionListener(RequestPermissionGrantedListener requestPermissionGrantedListener) {
            this.requestPermissionGrantedListener = requestPermissionGrantedListener;
            return this;
        }

        public Builder setPermissionListener(RequestPermissionDeniedListener requestPermissionDeniedListener) {
            this.requestPermissionDeniedListener = requestPermissionDeniedListener;
            return this;
        }

        public Builder setPermissionListener(RequestPermissionListener requestPermissionListener) {
            this.requestPermissionListener = requestPermissionListener;
            return this;
        }

        public Builder requestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public PermissionHelper build() {
            return new PermissionHelper(this);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.requestCode == requestCode) {
            List<String> permissionsGranted = new ArrayList<>();
            List<String> permissionsDenied = new ArrayList<>();
            int index = 0;
            for (String permission : permissions) {
                if (grantResults[index] == 0) {
                    permissionsGranted.add(permission);
                } else {
                    permissionsDenied.add(permission);
                }
                index++;
            }
            if (requestPermissionDeniedListener != null && permissionsDenied.size() > 0) {
                requestPermissionDeniedListener.hasDeniedPermission(permissionsDenied);
            }

            if (requestPermissionGrantedListener != null && permissionsGranted.size() > 0) {
                requestPermissionGrantedListener.hasPermission(permissionsGranted);
            }

            if (requestPermissionListener != null) {
                if (permissionsGranted.size() == permissions.length) {
                    requestPermissionListener.permissionsGranted();
                } else {
                    requestPermissionListener.permissionsDenied();
                }
            }

        }
    }

    private String getPermissionNames(List<String> permissions) {
        StringBuilder permissionNames = new StringBuilder();
        Context context = permisionHolder == PermisionHolder.ACTIVITY ? activityWeakReference.get() : fragmentWeakReference.get().getActivity();
        PackageManager packageManager = context.getPackageManager();
        for (String permission : permissions) {
            try {
                PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
                PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0);
                if (permissionNames.length() > 0) {
                    permissionNames.append(", ");
                    permissionNames.append(permissionGroupInfo.loadLabel(packageManager));
                } else {
                    permissionNames.append(permissionGroupInfo.loadLabel(packageManager));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return permissionNames.toString();
    }

    public void check() {
        if (!hasPermissions(permisionHolder.equals(PermisionHolder.ACTIVITY) ? activityWeakReference.get() : fragmentWeakReference.get().getActivity(), permissions)) {
            List<String> permissionNeedRationale = new ArrayList<>();
            for (String permission : permissions) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permisionHolder.equals(PermisionHolder.ACTIVITY)) {
                        if (activityWeakReference.get().shouldShowRequestPermissionRationale(permission)) {
                            permissionNeedRationale.add(permission);
                        }
                    } else {
                        if (fragmentWeakReference.get().shouldShowRequestPermissionRationale(permission)) {
                            permissionNeedRationale.add(permission);
                        }
                    }
                }
            }
            if (requestPermissionRationaleListener != null && permissionNeedRationale.size() > 0) {
                requestPermissionRationaleListener.shouldShowRequestPermissionRationale(getPermissionNames(permissionNeedRationale));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permisionHolder.equals(PermisionHolder.ACTIVITY)) {
                        activityWeakReference.get().requestPermissions(permissions, requestCode);
                    } else {
                        fragmentWeakReference.get().requestPermissions(permissions, requestCode);
                    }
                } else {
                    if (requestPermissionListener != null) {
                        requestPermissionListener.permissionsGranted();
                    }
                    if (requestPermissionGrantedListener != null) {
                        requestPermissionGrantedListener.hasPermission(Arrays.asList(permissions));
                    }
                }
            }
        } else {
            if (requestPermissionListener != null) {
                requestPermissionListener.permissionsGranted();
            }
            if (requestPermissionGrantedListener != null) {
                requestPermissionGrantedListener.hasPermission(Arrays.asList(permissions));
            }
        }
    }

}
