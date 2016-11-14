package org.ancode.alivelib.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.ancode.alivelib.utils.AliveLog;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyliu on 16-10-24.
 */
public class BaseStatsInfo {
//    JSONObject jsonObject = new JSONObject();
//    try {
//        jsonObject.put("device", Build.MODEL);
//        jsonObject.put("os", Build.DISPLAY);
//        //如果是加密电话
//        jsonObject.put("phone", "13018211911");
//    } catch (JSONException e) {
//        e.printStackTrace();
//    }
//    String tag = "MH:13018211911";


    //info
    String device = null;
    String idName = null;
    String id = null;
    String os = null;
    //stat
    String tag = null;

    public BaseStatsInfo(String tag) {
        this.tag = tag;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getIdName() {
        return idName == null ? "id" : idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public JSONObject getStatsInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", device);
            jsonObject.put("os", os);
            try {
                if (!TextUtils.isEmpty(idName) && !TextUtils.isEmpty(id)) {
                    jsonObject.put(idName, id);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            AliveLog.v("BaseStatsInfo", "获取数据失败");
            e.printStackTrace();
        }

        return jsonObject;
    }
}
