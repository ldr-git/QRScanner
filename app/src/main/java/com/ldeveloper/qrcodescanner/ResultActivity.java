package com.ldeveloper.qrcodescanner;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultActivity extends BaseActivity {

    @BindView(R.id.textViewResult)
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        toolbarShowBackWithTitle("Results");

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("text")) {
            result.setText(extras.getString("text"));
        }

    }
}
