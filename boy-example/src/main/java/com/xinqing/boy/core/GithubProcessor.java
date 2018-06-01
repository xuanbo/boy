package com.xinqing.boy.core;

import com.xinqing.boy.core.processor.AbstractProcessor;
import com.xinqing.boy.core.selector.Selector;
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
