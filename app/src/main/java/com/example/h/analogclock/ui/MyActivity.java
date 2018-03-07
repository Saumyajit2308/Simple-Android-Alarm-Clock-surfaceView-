package com.example.h.analogclock.ui;

import android.app.Activity;
import android.os.Bundle;

import com.example.h.analogclock.R;



public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MyFragment())
                    .commit();
        }
    }


}
