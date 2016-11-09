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
import org.ancode.alivelib.utils.AliveStatus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    Button aliveHelperActivity;
    Button onlygetdataBtn;
    Button aliveHelperNotify;
    TextView textview;
    Button aliveStatsActivity;
    Button aliveStatsNotify;
    Button close;

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
        AliveHelper.getHelper().closeAliveStats();
        //完全退出应用需要调用
        AliveHelper.release();
        super.onDestroy();
    }


    private void initView() {
        textview = (TextView) findViewById(R.id.textview);
        aliveHelperActivity = (Button) findViewById(R.id.alive_helper_acitivity);
        aliveHelperActivity.setOnClickListener(this);
        onlygetdataBtn = (Button) findViewById(R.id.onlygetdata);
        onlygetdataBtn.setOnClickListener(this);

        aliveHelperNotify = (Button) findViewById(R.id.alive_helper_notify);
        aliveHelperNotify.setOnClickListener(this);

        aliveStatsNotify = (Button) findViewById(R.id.alive_stats_notify);
        aliveStatsNotify.setOnClickListener(this);

        aliveStatsActivity = (Button) findViewById(R.id.alive_stats_activity);
        aliveStatsActivity.setOnClickListener(this);


        close = (Button) findViewById(R.id.close);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.onlygetdata:
                AliveHelper.getHelper()
                        .getUseGuideUrl(new StringCallBack() {
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
            case R.id.alive_helper_acitivity:
                AliveHelper.getHelper()
                        .showAliveUseGuide();
                break;

            case R.id.alive_helper_notify:
                AliveHelper.getHelper()
                        .notifyAliveUseGuide(2000);

                break;

            case R.id.alive_stats_activity:
                AliveHelper.getHelper().showAliveStats();
                break;
            case R.id.alive_stats_notify:
                AliveHelper.getHelper().notifyAliveStats(2000);
                break;
            case R.id.close:

                finish();
                break;

        }
    }

}
