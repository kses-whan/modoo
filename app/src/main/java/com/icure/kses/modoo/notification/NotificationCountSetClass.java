package com.icure.kses.modoo.notification;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.view.MenuItem;

/**
 * Created by priyankam on 19-07-2016.
 */
public class NotificationCountSetClass {
    private static LayerDrawable iconCart = null;
    private static LayerDrawable iconPush = null;

    public NotificationCountSetClass() {
        //constructor
    }

    public static void setAddToCart(Context context, MenuItem item, int numMessages) {
        iconCart = (LayerDrawable) item.getIcon();
        SetNotificationCount.setBadgeCount(context, iconCart, NotificationCountSetClass.setNotifyCount(numMessages));
    }

    public static void setAddToPushMsg(Context context, MenuItem item, int numMessages) {
        iconPush = (LayerDrawable) item.getIcon();
        SetNotificationCount.setBadgeCount(context, iconPush, NotificationCountSetClass.setNotifyCount(numMessages));
    }

    public static int setNotifyCount(int numMessages) {
        int count=numMessages;
        return count;

    }


}
