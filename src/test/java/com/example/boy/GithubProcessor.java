package com.example.boy;

import com.xinqing.boy.Item;
import com.xinqing.boy.Response;
import com.xinqing.boy.processor.AbstractProcessor;
import com.xinqing.boy.selector.Selector;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * GithubProcessor主要用于爬取个人Popular repositories
 * GithubRepositoryProcessor主要用于爬取repositories
 *
 * 首先从https://github.com/xuanbo找到Popular repositories的url
 * 然后Popular repositories的url进一步解析repositories(暂时只取title，readme)
 *
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
        next(links, new GithubRepositoryProcessor(), Collections.emptyMap());
    }

}

class GithubRepositoryProcessor extends AbstractProcessor {

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
