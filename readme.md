# boy project

[webmagic](https://github.com/code4craft/webmagic)项目的一个山寨版，供于学习。

## 特点

* Downloader组件
    * OkHttp3替换webmagic的HttpClient
    * 暂时只支持GET
* 加入Middleware组件
* 仅仅使用Jsoup解析Html


## 例子

```java
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

public class GithubProcessorTest {

    @Test
    public void run() {
        Spider.create(new GithubProcessor())
                // 限制领域
                .domain("github.com")
                // 开始url
                .addTargetUrl("https://github.com/xuanbo")
                .addPipeline(new ConsolePipeline())
                .thread(5)
                .run();
    }

}
```