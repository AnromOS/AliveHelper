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

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", Build.MODEL);
            jsonObject.put("os", Build.DISPLAY);
            //如果是加密电话
            jsonObject.put("phone", "13018211911");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String tag = "MH:13018211911";

        @Deprecated
        AliveHelper.getHelper().openAliveStats(jsonObject.toString(),tag);
        或者使用
        AliveHelper.getHelper().setAliveStatsInfo(jsonObject.toString());
        AliveHelper.getHelper().setAliveTag(tag);
        AliveHelper.getHelper().openAliveStats();

        //建议使用方法
        BaseStatsInfo statsInfo = new BaseStatsInfo();
        statsInfo.setDevice(Build.MODEL);
        statsInfo.setOs(Build.DISPLAY);
        statsInfo.setIdName("phone");
        statsInfo.setId("13018211911");
        statsInfo.setTag("MH:13018211911");

        AliveHelper.getHelper().openAliveStats(statsInfo);

        //关闭统计

        //AliveHelper.getHelper().closeAliveStats();

        - 注意:openAliveStats(jsonObject.toString())与setAliveStatsInfo(String info),info格式为json,json内参数内容不固定,需讨论.
####2).展示防杀指南相关接口
        //**********初始化完成后可以用在任何地方******//
        AliveHelper.getHelper().notification(int aftertime);//通知栏提示,点击跳转使用指南
        AliveHelper.getHelper().showAliveUseGuide();//直接打开使用指南