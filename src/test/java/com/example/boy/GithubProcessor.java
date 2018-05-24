package com.example.boy;

import com.xinqing.boy.Item;
import com.xinqing.boy.Response;
import com.xinqing.boy.processor.AbstractProcessor;
import com.xinqing.boy.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author xuan
 * @since 1.0.0
 */
public class GithubProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(GithubProcessor.class);

    @Override
    protected void doParse(Response response) {
        // 获取links
        List<String> links = Selector.jsoup(response)
                .select(".text-bold")
                .eachAttr("href");
        LOG.info("links: {}", links);
        // 供下次爬取
        nextUrls(links);
        // 数据
        Item item = Item.of().put(response.getUrl(), links.size());
        setItem(item);
    }

}
