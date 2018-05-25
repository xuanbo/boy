package com.xinqing.boy.selector;

import com.xinqing.boy.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 选择器
 *
 * @author xuan
 * @since 1.0.0
 */
public class Selector {

    /**
     * 获取Jsoup
     *
     * @param response Response
     * @return Element
     */
    public static Document jsoup(Response response) {
        return Jsoup.parse(response.getRawText());
    }

}
