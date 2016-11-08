package org.ancode.alivelib.crash;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Process;

import org.ancode.alivelib.callback.StringCallBack;
import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.utils.AliveLog;
import org.ancode.alivelib.utils.AliveSPUtils;
import org.ancode.alivelib.utils.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的 异常处理类 , 实现了 UncaughtExceptionHandler接口
 *
 * @author Administrator
 */
public class AliveHelperCrash implements UncaughtExceptionHandler {
    // 只有一个 AliveHelperCrash
    private static AliveHelperCrash crash;
    private Context context;
    private final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private AliveHelperCrash() {
    }

    public static synchronized AliveHelperCrash getInstance() {
        if (crash != null) {
            return crash;
        } else {
            crash = new AliveHelperCrash();
            return crash;
        }
    }

    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        // 这里将needUploadError 设置成false以关闭上传错误
        boolean needUploadError = true;
        AliveLog.e("aliveCrash", "对不起，" + Utils.getAppName() + "挂掉了= =!");
        try {
            // 1.获取当前程序的版本号. 版本的id
            String info = Utils.getVersionInfo();
            // 2.获取手机的硬件信息.
            String mobileInfo = Utils.getMobileInfo();
            // 3.把错误的堆栈信息 获取出来
            String errorinfo = getErrorInfo(arg1);
            String userid = null;
            try {
                userid = AliveSPUtils.getInstance().getASTag().split(":")[1];
            } catch (Exception e) {
                e.printStackTrace();
            }

            String date = dataFormat.format(new Date());
            AliveLog.e("error", "==> ERROR-BEGIN <================================================================");
            String errorText = "版本信息:\n" + info + "\n用户ID:\n" + userid + "\n时间:\n" + date + "\n手机信息:\n" + mobileInfo
                    + "\n错误信息:\n" + errorinfo;
            AliveLog.e("error", errorText);
            AliveLog.e("error", "==> ERROR-END   <================================================================");
            AliveLog.e("PrivPhone", errorText);
            // errorText = new
            // String(Base64.encodeBase64(errorText.getBytes()));
            if (needUploadError) {
                new postErrorInfoToServerTask().executeOnExecutor(TaskPool.AN_TASK_EXECUTOR, errorText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取错误的信息
     *
     * @param arg1
     * @return
     */
    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }


    private class postErrorInfoToServerTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            AliveLog.v("MyCrashHandler", "提交crash");
            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put("crash", params[0]);
                HttpClient.postCrash(map, new StringCallBack() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("ok")) {
                            AliveLog.v("MyCrashHandler", "提交成功 ");
                        } else {
                            AliveLog.v("MyCrashHandler", "提交失败 ");
                        }
                        Process.killProcess(Process.myPid());
//                        System.exit(0);
                    }

                    @Override
                    public void error(String error) {
                        AliveLog.v("MyCrashHandler", "提交失败 " + error);
                        Process.killProcess(Process.myPid());
//                        System.exit(0);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;

        }

    }
}