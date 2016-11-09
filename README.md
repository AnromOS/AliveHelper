# AliveHelper
AliveHelper防杀助手,统计应用使用率.


## 使用
***
###1.依赖权限
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
###2.Application中配置防杀助手(无耗时操作)

        //初始化操作
        AliveHelper.init(getApplicationContext());//初始化(必须项)
        AliveHelper.setNotifySmallIcon(R.drawable.alive_helper_small_icon);//如果要弹出通知,需开发者提供应用小图标id
        AliveHelper.setDebug(true);//是否打印防杀助手log
        AliveHelper.setThemeColor(R.color.alive_dialog_btn_border_color);//手动设置展示界面的主色调
        AliveHelper.useAnet(false); //是否使用原网环境
###3.方法使用

        功能类,可以在Application初始化完成之后,任意地方使用,根据实际场景进行使用

####1).开启使用率统计相关

        String tag = "MH:13018211911";//使用当前应用的某个用户的唯一标示,格式例如 应用拼音简称:账号 (必填项)
        BaseStatsInfo statsInfo = new BaseStatsInfo(tag);
        statsInfo.setIdName("phone");//id名称 (非必填项)
        statsInfo.setId("13018211911");//id值(非必填项)
        //开启统计
        AliveHelper.getHelper().openAliveStats(statsInfo);

        //关闭统计
        AliveHelper.getHelper().closeAliveStats();

####2).展示防杀指南相关接口
        //**********初始化完成后可以用在任何地方******//
        AliveHelper.getHelper().notifyAliveUseGuide(int aftertime);//通知栏提示,点击跳转防杀指南
        AliveHelper.getHelper().showAliveUseGuide();//直接打开防杀指南
####3).查看保活统计
        AliveHelper.getHelper().notifyAliveStats(int aftertime);//通知栏提示,点击跳转保活统计
        AliveHelper.getHelper().showAliveStats();//直接打开保活统计界面