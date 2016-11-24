package org.ancode.alivehelperdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ancode.alivelib.AliveHelper;
import org.ancode.alivelib.callback.StringCallBack;
import org.ancode.alivelib.utils.AliveDateUtils;
import org.ancode.alivelib.utils.AliveLog;
import org.ancode.alivelib.utils.AliveSPUtils;
import org.ancode.alivelib.utils.AliveTestUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    Button aliveHelperActivity;
    Button onlygetdataBtn;
    Button aliveHelperNotify;
    TextView textview;
    Button aliveStatsActivity;
    Button aliveStatsNotify;
    Button close;
    Button close_alive_stats;
    Button open_alive_stats;
    Button checkAliveStatsAlive;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        verifyStoragePermissions(this);
        //initPermission();
    }
//
//    private void initPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(MainActivity.this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 10);
//            }
//        }
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.M)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 10) {
//            if (Settings.canDrawOverlays(this)) {
//                AliveLog.v(TAG, "SYSTEM_ALERT_WINDOW 授权成功...");
//                initPermission();
//            } else {
//                AliveLog.v(TAG, "SYSTEM_ALERT_WINDOW 授权失败...");
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        AliveHelper.getHelper().closeAliveStats();
        //完全退出应用需要调用
        AliveHelper.killAliveHelper();
        android.os.Process.killProcess(android.os.Process.myPid());
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

        close_alive_stats = (Button) findViewById(R.id.close_alive_stats);
        close_alive_stats.setOnClickListener(this);
        open_alive_stats = (Button) findViewById(R.id.open_alive_stats);
        open_alive_stats.setOnClickListener(this);
        checkAliveStatsAlive = (Button) findViewById(R.id.check_alivestats_alive);
        checkAliveStatsAlive.setOnClickListener(this);
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
            case R.id.close_alive_stats:
                AliveHelper.getHelper().closeAliveStats();
                break;
            case R.id.open_alive_stats:
                Toast.makeText(this, "暂无此方法", Toast.LENGTH_SHORT).show();
//                AliveHelper.getHelper().openAliveStats();
                break;
            case R.id.check_alivestats_alive:
//                long time = AliveSPUtils.getInstance().getNextShowAsNotifyTime();
//                long tim2 = AliveDateUtils.getThisDayStartTime(time);
//                Toast.makeText(this, AliveDateUtils.timeFormat(tim2, AliveDateUtils.DEFAULT_FORMAT), Toast.LENGTH_SHORT).show();
//                if (AliveHelper.getHelper().checkStatsIsAlive()) {
//                    Toast.makeText(this, "还活着", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "已经死了", Toast.LENGTH_SHORT).show();
//                }
//                Toast.makeText(this, "暂无此方法", Toast.LENGTH_SHORT).show();
//                AliveHelper.getHelper().openAliveStats();
                break;
            case R.id.close:

                finish();
                break;

        }
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }


}
