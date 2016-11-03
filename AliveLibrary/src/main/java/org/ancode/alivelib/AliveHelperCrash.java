package org.ancode.alivelib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.utils.AliveLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        AliveLog.e("aliveCrash", "对不起，AliveHelper挂掉了= =!");
        try {
            // 1.获取当前程序的版本号. 版本的id
            String info = getVersionInfo();
            // 2.获取手机的硬件信息.
            String mobileInfo = getMobileInfo();
            // 3.把错误的堆栈信息 获取出来
            String errorinfo = getErrorInfo(arg1);

            String date = dataFormat.format(new Date());
            AliveLog.e("aliveCrash", "==> ERROR-BEGIN <================================================================");
            String errorText = "版本信息:\n" + info + "\n时间:\n" + date + "\n手机信息:\n" + mobileInfo
                    + "\n错误信息:\n" + errorinfo;

            AliveLog.e("aliveCrash", errorText);
            AliveLog.e("aliveCrash", "==> ERROR-END   <================================================================");
            if (crash != null) {
                try {
                    File file = new File(context.getFilesDir(), HelperConfig.ALIVE_HELPER_CRASH_FILE_NAME);


                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            AliveLog.e("aliveCrash", "create file error:" + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }

                    //将crash log写入文件
                    FileOutputStream fileOutputStream = context.openFileOutput(HelperConfig.ALIVE_HELPER_CRASH_FILE_NAME,Context.MODE_PRIVATE);
                    PrintStream printStream = new PrintStream(fileOutputStream);
                    arg1.printStackTrace(printStream);
                    printStream.flush();
                    printStream.close();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // errorText = new
            // String(Base64.encodeBase64(errorText.getBytes()));
        } catch (
                Exception e
                )

        {
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

    /**
     * 获取手机的硬件信息
     *
     * @return
     */
    private String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        // 通过反射获取系统的硬件信息
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                // 暴力反射 ,获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取手机的版本信息
     *
     * @return
     */
    private String getVersionInfo() {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName + " - " + info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

}