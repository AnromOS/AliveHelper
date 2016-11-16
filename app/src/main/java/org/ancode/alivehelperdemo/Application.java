package org.ancode.alivehelperdemo;

import android.os.Build;
import android.util.Log;

import org.ancode.alivelib.AliveHelper;
import org.ancode.alivelib.bean.BaseStatsInfo;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyliu on 16-8-25.
 */
public class Application extends android.app.Application {
    private static android.app.Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        init();
    }

    public static android.app.Application getInstance() {
        return application;
    }

    private void init() {

        //初始化操作(无耗时操作)

        AliveHelper.init(getApplicationContext());//初始化**(必须项)**
        Log.v("application", "init aliveHelper");
        AliveHelper.setNotifySmallIcon(R.drawable.alive_helper_small_icon);//如果要弹出通知,需开发者提供应用小图标
        AliveHelper.setDebug(true);//是否打印防杀助手log
//        AliveHelper.setThemeColor(R.color.alive_dialog_btn_border_color);//手动设置展示界面的主色调
//        AliveHelper.useAnet(false); //是否使用原网环境
        AliveHelper.isRelease(false);
        //*****开启使用率统计相关******//


        //使用当前应用的某个用户的唯一标示,例如 应用拼音简称:账号 (必填项)
        String tag = "MH:13018211911";
        BaseStatsInfo statsInfo = new BaseStatsInfo(tag);
//        statsInfo.setIdName("phone");//id名称 (非必填项)
//        statsInfo.setId("13018211911");//id值(非必填项)
        //开启统计
        AliveHelper.getHelper().openAliveStats(statsInfo);
        //或者使用
//        AliveHelper.getHelper().setAliveStatsInfo(statsInfo.getStatsInfo().toString());
//        AliveHelper.getHelper().setAliveTag(statsInfo.getTag());
//        AliveHelper.getHelper().openAliveStats();

        //关闭统计
        //AliveHelper.getHelper().closeAliveStats();

        //- 注意:setAliveStatsInfo(String info),info格式为json,json内参数内容不固定,需讨论.
    }
}
