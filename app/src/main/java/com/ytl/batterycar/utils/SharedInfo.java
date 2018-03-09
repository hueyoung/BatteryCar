package com.ytl.batterycar.utils;

/**
 * Author: HueYoung
 * E-mail: yangtaolue@xuechengjf.com
 * Date: 16/3/18 下午3:09
 * <p/>
 * Description: SharedPreferences管理类
 * 需要持久化的数据:
 * 1.User 信息 (Token)
 * 2.本地的公告时间戳
 * 3.本地的手势密码
 * 4.重新打开的时间
 * <p/>
 * 临时的数据:
 * 1.服务器的公告时间戳
 * 2.购买的信息
 */
public class SharedInfo {
    private SoftMap<String, Object> moMap;

    private SharedInfo() {
    }

    public static SharedInfo getInstance() {
        return SharedInfoInstance.instance;
    }

    private static class SharedInfoInstance {
        static SharedInfo instance = new SharedInfo();
    }

    /**
     * 获取Sp 实体类
     */
    public <T> T getValue(Class<T> clazz) {
        if (null == clazz) {
            return null;
        }
        String key = getKey(clazz);
        // moMap 中是否存在，存在则从 moMap 中取出
        if (getMoMap().containsKey(key)) {
            return (T) moMap.get(key);
        }
        // 不存在则从 SP 中取出
        T mo = SPUtil.getEntity(clazz, null, true);
        // 不为空，则 put 进 moMap
        if (null != mo) {
            moMap.put(key, mo);
        }
        return mo;
    }

    /**
     * 将mo存储至SoftMap 中，防止每次使用重新创建
     */
    public void setValue(Class<?> clazz, Object obj) {
        if (null == clazz && null == obj) {
            return;
        }
        // 每次setValue 替换最新的Object
        String key = getKey(clazz);
        if (getMoMap().containsKey(key)) {
            getMoMap().remove(key);
        }
        getMoMap().put(key, obj);
        SPUtil.setEntity(obj, true);
    }

    /**
     * 从mo中删除此键值对
     */
    public void remove(Class<?> clazz) {
        if (null == clazz) {
            return;
        }
        String key = getKey(clazz);
        getMoMap().remove(key);
        SPUtil.remove(key);
    }

    private String getKey(Class<?> clazz) {
        return clazz.getName();
    }

    private SoftMap<String, Object> getMoMap() {
        if (moMap == null)
            moMap = new SoftMap<>();
        return moMap;
    }

    private void setMoMap(SoftMap<String, Object> moMap) {
        this.moMap = moMap;
    }
}