package com.sendbird.android.sample;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

public class ReportActivity extends Activity {

    private ImageButton mBtnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mBtnClose = (ImageButton)findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
