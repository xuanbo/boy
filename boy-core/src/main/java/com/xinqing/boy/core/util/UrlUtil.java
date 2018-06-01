package com.xinqing.boy.core.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * url utils
 *
 * @author xuan
 * @since 1.0.0
 */
public final class UrlUtil {

    private UrlUtil() {
    }

    public static String getAbsoluteUrl(String absolutePath, String relativePath) {
        try {
            return new URL(new URL(absolutePath), relativePath).toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPath(String url) {
        try {
            return new URL(url).getPath();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDomain(String url) {
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
