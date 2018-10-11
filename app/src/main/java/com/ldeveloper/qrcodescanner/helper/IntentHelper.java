package com.ldeveloper.qrcodescanner.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class IntentHelper {

    private IntentHelper(Builder builder) {

        switch (builder.intentType) {
            case ACTIVITY: {
                Intent intent = new Intent(builder.activity.get(), builder.cls);
                if (builder.extras != null) {
                    intent.putExtras(builder.extras);
                }
                if (builder.mainPage) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
                if (builder.withAnimation) {
                    builder.activity.get().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                if (builder.requestCode != Integer.MIN_VALUE) {
                    builder.activity.get().startActivityForResult(intent, builder.requestCode);
                    return;
                }
                builder.activity.get().startActivity(intent);
                break;
            }
            case FRAGMENT: {
                Intent intent = new Intent(builder.fragment.get().getActivity(), builder.cls);
                if (builder.extras != null) {
                    intent.putExtras(builder.extras);
                }
                if (builder.mainPage) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
                if (builder.withAnimation) {
                    builder.fragment.get().getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                if (builder.requestCode != Integer.MIN_VALUE) {
                    builder.fragment.get().startActivityForResult(intent, builder.requestCode);
                    return;
                }
                builder.fragment.get().startActivity(intent);
                break;
            }
        }
    }

    private enum IntentType {
        ACTIVITY,
        FRAGMENT
    }

    public static class Builder {
        private WeakReference<Activity> activity;
        private WeakReference<Fragment> fragment;
        private Class<?> cls;
        private Bundle extras;
        private boolean mainPage;
        private boolean withAnimation;
        private int requestCode = Integer.MIN_VALUE;
        private IntentType intentType;

        public Builder(Activity activity) {
            intentType = IntentType.ACTIVITY;
            this.activity = new WeakReference<>(activity);
        }

        public Builder(Fragment fragment) {
            intentType = IntentType.FRAGMENT;
            this.fragment = new WeakReference<>(fragment);
        }

        public Builder toClass(Class<?> cls) {
            this.cls = cls;
            return this;
        }

        public Builder putExtras(Bundle extras) {
            this.extras = extras;
            return this;
        }

        public Builder putExtra(String key, Serializable object) {
            if (this.extras == null)
                this.extras = new Bundle();
            this.extras.putSerializable(key, object);
            return this;
        }

        public Builder putExtra(String key, Parcelable object) {
            if (this.extras == null)
                this.extras = new Bundle();
            this.extras.putParcelable(key, object);
            return this;
        }

        public Builder putExtra(String key, String object) {
            if (this.extras == null)
                this.extras = new Bundle();
            this.extras.putString(key, object);
            return this;
        }

        public Builder putExtra(String key, boolean object) {
            if (this.extras == null)
                this.extras = new Bundle();
            this.extras.putBoolean(key, object);
            return this;
        }

        public Builder putExtra(String key, int object) {
            if (this.extras == null)
                this.extras = new Bundle();
            this.extras.putInt(key, object);
            return this;
        }

        public Builder putSerializableExtra(String key, Serializable object) {
            if (this.extras == null)
                this.extras = new Bundle();
            this.extras.putSerializable(key, object);
            return this;
        }

        public Builder isMainPage(boolean mainPage) {
            this.mainPage = mainPage;
            return this;
        }

        public Builder isMainPage() {
            this.mainPage = true;
            return this;
        }

        public Builder withAnimation(boolean withAnimation) {
            this.withAnimation = withAnimation;
            return this;
        }

        public Builder requestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public IntentHelper show() {
            return new IntentHelper(this);
        }

    }

}
