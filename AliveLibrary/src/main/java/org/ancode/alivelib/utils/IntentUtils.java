package org.ancode.alivelib.utils;

import android.content.Intent;

import org.ancode.alivelib.config.HelperConfig;

/**
 * Created by andyliu on 16-8-26.
 */
public class IntentUtils {

    public static Intent getNormalActivity(Class<?> cls, boolean applyStatusColor) {
        Intent intent = new Intent(HelperConfig.CONTEXT, cls);
        intent.putExtra(HelperConfig.APPLY_STATUS_COLOR, applyStatusColor);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }


    public static Intent getNotifyActivity(Class<?> cls) {
        Intent intent = new Intent(HelperConfig.CONTEXT, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

}