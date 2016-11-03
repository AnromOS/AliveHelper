package org.ancode.alivehelperdemo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ancode.alivelib.AliveHelper;
import org.ancode.alivelib.callback.StringCallBack;
import org.ancode.alivelib.utils.AliveLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    Button showActivityBtn;
    Button onlygetdataBtn;
    Button notificationGoActivity;
    TextView textview;
    Button showAliveStats;
    //about broadcast
    public static final String BROADCAST_ACTION = "org.ancode.test.ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        //initPermission();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Settings.canDrawOverlays(this)) {
                AliveLog.v(TAG, "SYSTEM_ALERT_WINDOW 授权成功...");
                initPermission();
            } else {
                AliveLog.v(TAG, "SYSTEM_ALERT_WINDOW 授权失败...");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        textview = (TextView) findViewById(R.id.textview);
        showActivityBtn = (Button) findViewById(R.id.showactivity);
        showActivityBtn.setOnClickListener(this);
        onlygetdataBtn = (Button) findViewById(R.id.onlygetdata);
        onlygetdataBtn.setOnClickListener(this);
        notificationGoActivity = (Button) findViewById(R.id.go_activity_notification);
        notificationGoActivity.setOnClickListener(this);
        notificationGoActivity = (Button) findViewById(R.id.go_activity_notification);
        notificationGoActivity.setOnClickListener(this);
        showAliveStats = (Button) findViewById(R.id.go_alive_stats_activity);
        showAliveStats.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.onlygetdata:
                AliveHelper.getHelper()
                        .check(new StringCallBack() {
                            @Override
                            public void onResponse(String response) {
                                textview.setText("StringCallBack function url==" + response);
                            }

                            @Override
                            public void error(String error) {
                                textview.setText("StringCallBack function error\n" + error);
                            }
                        });

                break;
            case R.id.showactivity:
                AliveHelper.getHelper()
                        .showAliveUseGuide();
                break;

            case R.id.go_activity_notification:
                AliveHelper.getHelper()
                        .notifyAliveUseGuide(2000);

                break;

            case R.id.go_alive_stats_activity:
                AliveHelper.getHelper().showAliveStats();
                break;

        }
    }

}
