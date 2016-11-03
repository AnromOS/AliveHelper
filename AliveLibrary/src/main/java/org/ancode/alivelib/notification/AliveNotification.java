/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ancode.alivelib.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.NotificationCompat;

import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.utils.AliveLog;

public class AliveNotification {
    private static final String TAG = AliveNotification.class.getSimpleName();
    private String titleStr;
    private String textStr;
    private String tickerTextStr;
    private int mLargeIcon = -1, mSmallIcon = -1;

    private Bitmap bLargeIcon;
    private NotificationManager manager;
    private static final int NOTIFY_FLAG = 0x1101;
    private PendingIntent pendingIntent = null;

    public AliveNotification() {
        initManager();
    }

    public void show() {
        show(null);
    }

    public void show(Intent intent) {
        CharSequence tickerText;
        CharSequence contentTitle;
        CharSequence contentText;
        Bitmap largeIcon;
        tickerText = tickerTextStr;
        contentTitle = titleStr;
        contentText = textStr;
        if (mLargeIcon == -1) {
            largeIcon = bLargeIcon;
        } else {

            largeIcon = ((BitmapDrawable) HelperConfig.CONTEXT.getResources().getDrawable(mLargeIcon)).getBitmap();
        }

        try {
            if (intent != null)
                setIntent(intent);
            Notification notification = createBuilder(largeIcon, mSmallIcon,
                    contentTitle, contentText, tickerText, System.currentTimeMillis(), true, false, true, pendingIntent);
            //放置在正在运行栏目中
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            manager.notify(NOTIFY_FLAG, notification);
            AliveLog.v(TAG, "notification is show");
        } catch (Exception e) {
            AliveLog.e(TAG, "notification is error\n" + e.getLocalizedMessage());
        }


    }

    public void cancelAll() {
        if (manager != null)
            manager.cancelAll();

    }

    public void cancelAliveHelper() {
        if (manager != null)
            manager.cancel(NOTIFY_FLAG);
    }

    private Notification createBuilder(Bitmap largeIcon, int smallIcon, CharSequence contentTitle, CharSequence contentText, CharSequence ticker,
                                       long when, boolean onGoing, boolean autoCancel, boolean onlyAlertOnce, PendingIntent pendingIntent) {
        NotificationCompat.Builder nb = new NotificationCompat.Builder(HelperConfig.CONTEXT);
        nb.setDefaults(Notification.DEFAULT_SOUND);
        nb.setContentTitle(contentTitle);
        nb.setContentText(contentText);
        nb.setTicker(ticker);
        nb.setWhen(when);
        nb.setOngoing(onGoing);
        //heads up http://www.jianshu.com/p/4d76b2bc8784
        //nb.setFullScreenIntent(pendingIntent, true);
        nb.setContentIntent(pendingIntent);

        nb.setAutoCancel(autoCancel);
        nb.setOnlyAlertOnce(onlyAlertOnce);
        nb.setSmallIcon(smallIcon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nb.setLargeIcon(largeIcon);
        }
        return nb.build();
    }

    public AliveNotification setPedingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
        return this;
    }

    public AliveNotification setIntent(Intent intent) {
        pendingIntent = PendingIntent.getActivity(HelperConfig.CONTEXT, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return this;
    }

    protected NotificationManager initManager() {
        if (manager == null) {
            manager = (NotificationManager) HelperConfig.CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }


    public String getTitle() {
        return titleStr;
    }

    public AliveNotification setTitle(String title) {
        this.titleStr = title;
        return this;
    }

    public String getText() {
        return textStr;
    }

    public AliveNotification setText(String text) {
        this.textStr = text;
        return this;
    }

    public String getTickerText() {
        return tickerTextStr;
    }

    public AliveNotification setTickerText(String tickerText) {
        this.tickerTextStr = tickerText;
        return this;
    }


    public int getLargeIcon() {
        return mLargeIcon;
    }

    public AliveNotification setLargeIcon(int mLargeIcon) {
        this.mLargeIcon = mLargeIcon;
        return this;
    }


    public AliveNotification setLargeIcon(Bitmap bLargeIcon) {
        this.bLargeIcon = bLargeIcon;
        return this;
    }

    public int getSmallIcon() {
        return mSmallIcon;
    }

    public AliveNotification setSmallIcon(int mSmallIcon) {
        this.mSmallIcon = mSmallIcon;
        return this;
    }


}