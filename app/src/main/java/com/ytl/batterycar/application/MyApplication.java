package com.ytl.batterycar.application;

import android.app.Application;

/**
 * Author: HueYoung
 * E-mail: yangtaolue@xuechengjf.com
 * Date: 2016/2/17 14:41
 * <p/>
 * Description: Application
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    /** application单例 */
    private static MyApplication     mInstance;

    /**
     * 单例模式中获取唯一的ZRJFApplication实例
     *
     * @return Application
     */
    public static MyApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}