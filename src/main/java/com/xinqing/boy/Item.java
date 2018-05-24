package com.xinqing.boy;

import java.util.HashMap;
import java.util.Map;

/**
 * 条目
 *
 * @author xuan
 * @since 1.0.0
 */
public class Item {

    /**
     * 保存item
     */
    private Map<String, Object> pairs = new HashMap<>();

    public static Item of() {
        return new Item();
    }

    /**
     * 根据key获取value
     *
     * @param key 设置的key
     * @return value
     */
    public Object get(String key) {
        return pairs.get(key);
    }

    /**
     * 根据key获取int value
     *
     * @param key 设置的key
     * @return value
     */
    public Integer getInt(String key) {
        Object o = pairs.get(key);
        if (o == null) {
            return null;
        }
        return (Integer) o;
    }

    /**
     * 根据key获取long value
     *
     * @param key 设置的key
     * @return value
     */
    public Long getLong(String key) {
        Object o = pairs.get(key);
        if (o == null) {
            return null;
        }
        return (Long) o;
    }

    /**
     * 根据key获取string value
     *
     * @param key 设置的key
     * @return value
     */
    public String getString(String key) {
        Object o = pairs.get(key);
        if (o == null) {
            return null;
        }
        return (String) o;
    }

    /**
     * 获取所有item
     *
     * @return Map<String, Object>
     */
    public Map<String, Object> getAll() {
        return pairs;
    }

    /**
     * 设置key，value
     *
     * @param key key
     * @param value value
     * @return Item
     */
    public Item put(String key, Object value) {
        pairs.put(key, value);
        return this;
    }

}
