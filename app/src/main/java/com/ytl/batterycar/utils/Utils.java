package com.ytl.batterycar.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Author: HueYoung
 * E-mail: yangtaolue@xuechengjf.com
 * Date: 2016/2/23 14:55
 * <p/>
 * Description:
 */
public class Utils {

    /**
     * toast 过滤 5s内禁止重复出现
     */
    private static final long                  Interval = 3 * 1000;
    private static final SoftMap<String, Long> map      = new SoftMap<>();
    private static Toast CURR_TOAST;

    public static void toast(Context context, String msg) {
        long preTime = 0;
        if (map.containsKey(msg)) {
            preTime = map.get(msg);
        }
        final long now = System.currentTimeMillis();
        if (now >= preTime + Interval) {
            if (CURR_TOAST != null) {
                CURR_TOAST.cancel();
            }
            if (context != null) {
                Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 520);
                toast.show();
                map.put(msg, now);
                CURR_TOAST = toast;
            }
        }
    }

    public static void toast(Activity activity, int id) {
        toast(activity, activity.getString(id));
    }

    public static void toast(Activity activity, String msg) {
        toast(activity, msg);
    }
}