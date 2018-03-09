package com.ytl.batterycar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ytl.batterycar.application.MyApplication;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: HueYoung
 * E-mail: yangtaolue@xuechengjf.com
 * Date: 2016/2/23 19:30
 * <p/>
 * Description: SharePreference工具类
 */
public class SPUtil {
    private SharedPreferences sp;
    /**
     * 上下文
     */
    private Context           context;
    /**
     * 保存在手机里面的文件名
     */
    private final static String SP_NAME = "com.ytl.batterycar.sp";
    private final static String SECRET_KEY = "6C4E60E55552386C759569836DC0F83869836DC0F838C0F7";

    private SPUtil() {
        context = MyApplication.getInstance();
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPUtil getInstance() {
        return SPUtilInstance.instance;
    }

    private static class SPUtilInstance {
        static SPUtil instance = new SPUtil();
    }

    private SharedPreferences getSp() {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     *         键值对的key
     * @param value
     *         键值对的值
     *
     * @return 是否保存成功
     */
    public static boolean setValue(String key, Object value) {
        Editor edit = SPUtil.getInstance().getSp().edit();

        if (value instanceof String) {
            return edit.putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            return edit.putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Float) {
            return edit.putFloat(key, (Float) value).commit();
        } else if (value instanceof Integer) {
            return edit.putInt(key, (Integer) value).commit();
        } else if (value instanceof Long) {
            return edit.putLong(key, (Long) value).commit();
        } else if (value instanceof Set) {
            throw new IllegalArgumentException("Value can not be Set object!");
        }
        return false;
    }

    public static Object getValue(String key) {
        Map<String, ?> map = getAll();
        if (contains(key)) {
            return map.get(key);
        }
        return null;
    }

    /**
     * 得到Boolean类型的值
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return SPUtil.getInstance().getSp().getBoolean(key, defaultValue);
    }

    /**
     * 得到String类型的值
     */
    public static String getString(String key, String defaultValue) {
        return SPUtil.getInstance().getSp().getString(key, defaultValue);
    }

    /**
     * 得到Float类型的值
     */
    public static Float getFloat(String key, float defaultValue) {
        return SPUtil.getInstance().getSp().getFloat(key, defaultValue);
    }

    /**
     * 得到Int类型的值
     */
    public static int getInt(String key, int defaultValue) {
        return SPUtil.getInstance().getSp().getInt(key, defaultValue);
    }

    /**
     * 得到Long类型的值
     */
    public static long getLong(String key, long defaultValue) {
        return SPUtil.getInstance().getSp().getLong(key, defaultValue);
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static boolean remove(String key) {
        Editor editor = SPUtil.getInstance().getSp().edit();
        editor.remove(key);
        return editor.commit();
    }

    /**
     * @param obj
     *         移除对象
     *
     * @return 是否remove
     */
    public static boolean remove(Object obj) {
        return obj != null && remove(getKey(obj.getClass()));
    }

    /**
     * 清除所有数据
     *
     * @return 是否成功
     */
    public static boolean clear() {
        Editor editor = SPUtil.getInstance().getSp().edit();
        editor.clear();
        return editor.commit();
    }

    /**
     * 查询某个key是否已经存在
     *
     * @return 是否存在
     */
    public static boolean contains(String key) {
        return SPUtil.getInstance().getSp().contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String, ?> getAll() {
        return SPUtil.getInstance().getSp().getAll();
    }
    ///////////////////////////////////////////////////////////////////////////
    // Object 以参数名的形式存入SP
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 保存指定对象至 SharedPreferences
     */
    public static void setObject(Object obj) {
        try {
            RefObject object = RefObject.wrap(obj);
            List<RefObject> list   = object.getAll();
            for (RefObject field : list) {
                Object value = field.unwrap();
                if (value != null) {
                    setValue(field.getName(), field.unwrap());
                } else {
                    // 值为空时，清除
                    remove(field.getName());
                }
            }
        } catch (RefException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从SharedPreferences读取指定Object
     *
     * @return T
     */
    public static <T> T getObject(Class<T> clazz) {
        try {
            T               obj    = clazz.newInstance();
            RefObject       object = RefObject.wrap(obj);
            List<RefObject> list   = object.getAll();
            for (RefObject field : list) {
                Object value = getValue(field.getName());
                //当值为null时不予赋值
                if (value != null) {
                    field.set(getValue(field.getName()));
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Object 以 JSON 的形式存入SP
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param obj
     *         对象
     *
     * @return 是否保存成功
     */
    public static boolean setEntity(final Object obj) {
        return setEntity(obj, false);
    }

    /**
     * @param obj
     *         对象
     * @param encryption
     *         是否需要加密
     *
     * @return 是否保存成功
     */
    public static boolean setEntity(final Object obj, boolean encryption) {
        if (obj != null) {
            final String innerKey = getKey(obj.getClass());
            if (innerKey != null) {
                String value = obj2str(obj);
                if (TextUtils.isEmpty(value)) {
                    return false;
                }
                if (encryption) {
                    try {
                        value = ThreeDESUtil.encrypt(SECRET_KEY, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return setValue(innerKey, value);
            }
        }
        return false;
    }

    public static boolean removeEntity(Class<?> clazz) {
        return remove(clazz.getClass());
    }

    /**
     * @param clazz
     *         类型
     * @param defaultValue
     *         默认值
     *
     * @return T对象
     */
    public static <T> T getEntity(Class<T> clazz, T defaultValue) {
        return getEntity(clazz, defaultValue);
    }

    /**
     * @param clazz
     *         类型
     * @param defaultValue
     *         默认值
     * @param decryption
     *         是否需要解密
     *
     * @return T对象
     */
    public static <T> T getEntity(final Class<T> clazz, final T defaultValue, boolean decryption) {
        final String innerKey = getKey(clazz);
        if (!TextUtils.isEmpty(innerKey)) {
            String value = getString(innerKey, null);
            if (TextUtils.isEmpty(value)) {
                return null;
            }
            if (decryption) {
                try {
                    value = ThreeDESUtil.decrypt(SECRET_KEY, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            T ret = str2obj(value, clazz);
            if (ret != null) {
                return ret;
            }
        }
        return defaultValue;
    }

    /**
     * 类对应的key
     */
    private static String getKey(final Class<?> clazz) {
        if (clazz != null) {
            return clazz.getName();
        }
        return null;
    }

    /***
     * Object 到 String 的序列化
     */
    private static String obj2str(final Object obj) {
        try {
            return new Gson().toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * String 到 Object 的反序列化
     */
    private static <T> T str2obj(final String string, final Class<T> clazz) {
        try {
            return new Gson().fromJson(string, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}