package com.xinqing.boy.core;

import com.xinqing.boy.core.processor.AbstractProcessor;
import com.xinqing.boy.core.selector.Selector;
import org.jsoup.nodes.Document;

/**
 * @author xuan
 * @since 1.0.0
 */
public class GithubRepositoryProcessor extends AbstractProcessor {

    @Override
    protected void doParse(Response response) {
        Document document = Selector.jsoup(response);
        String title = document.title();
        String readme = document.select("#readme").html();

        // 数据
        Item item = Item.of()
                .put("title", title)
                .put("readme", readme);
        setItem(item);
    }

}
