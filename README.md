# AliveHelper
AliveHelper防杀助手,统计应用使用率.

## Gradle Dependency
#### appcompat-v7版本>23.2.1以下方式引入
    compile ('org.ancode.library:AliveLibrary:0.1.10') {
            exclude group:'com.android.support', module:'appcompat-v7'
        }
#### 如果项目中没有appcompat-v7或者版本<=23.2.1
    compile 'org.ancode.library:AliveLibrary:0.1.10'
## 使用

### 1.依赖权限
        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
### 2.Application中配置防杀助手(无耗时操作)

        //初始化(必须项)
        AliveHelper.init(getApplicationContext());
        //如果要弹出通知,需开发者提供应用小图标id
        AliveHelper.setNotifySmallIcon(R.drawable.alive_helper_small_icon);
        //是否打印防杀助手log
        AliveHelper.setDebug(true);
        //手动设置展示界面的主色调
        AliveHelper.setThemeColor(R.color.alive_dialog_btn_border_color);
        //是否使用原网环境
        AliveHelper.useAnet(false);
        //是否是外部版(默认是 外部版);
        AliveHelper.isRelease(false);
        //释放AliveHelper
        AliveHelper.killAliveHelper();

### 3.方法使用

1. 开启使用率统计相关

        //使用当前应用的某个用户的唯一标示,格式例如 应用拼音简称:账号 (必填项)
        String tag = "MH:13018211911";
        BaseStatsInfo statsInfo = new BaseStatsInfo(tag);
        //id名称 (非必填项)
        statsInfo.setIdName("phone");
        //id值(非必填项)
        statsInfo.setId("13018211911");
        //开启统计
        AliveHelper.getHelper().openAliveStats(statsInfo);
        //关闭统计
        AliveHelper.getHelper().closeAliveStats();

2. 展示防杀指南相关接口

        //通知栏提示,点击跳转防杀指南
        AliveHelper.getHelper().notifyAliveUseGuide(int aftertime);
        //直接打开防杀指南
        AliveHelper.getHelper().showAliveUseGuide();
3. 查看保活统计

        //通知栏提示,点击跳转保活统计
        AliveHelper.getHelper().notifyAliveStats(int aftertime);
        //直接打开保活统计界面
        AliveHelper.getHelper().showAliveStats();