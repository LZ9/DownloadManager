package com.snxun.downloaddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * 主界面
 * Created by zhouL on 2016/11/22.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("testtag", "currentTimeMillis: " + System.currentTimeMillis());
    }


}
