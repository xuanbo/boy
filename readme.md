# boy project

[webmagic](https://github.com/code4craft/webmagic)项目的一个山寨版，供于学习。

## 对比webmagic

* Downloader组件
    * OkHttp3替换webmagic的HttpClient
    * 暂时只支持GET
* 仅仅使用Jsoup解析Html
* 加入Middleware组件，在Downloader前对Request进行定制
* 加入请求回调解析，后续获取的url交给不同的Processor


## 例子

github个人面板Popular repositories信息获取：

### processor
```java
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
```

### run

```java
public class GithubProcessorTest {

    @Test
    public void run() {
        Spider.create(new GithubProcessor())
                .domain("github.com")
                .addTargetUrl("https://github.com/xuanbo")
                .addPipeline(new TextPipeline("D:\\developer\\Code\\boy\\github.txt"))
                .addPipeline(new ConsolePipeline())
                .thread(5)
                .retry(1)
                .sleep(500)
                .run();
    }

}
```

## why boy project？

在编写爬虫时，就拿获取商品而言，我们肯定不是漫无目的的爬取网页。

而是有一些目的：

* 从商品列表页出发，找到商品信息url
* 再根据商品信息url爬取详情页
* 分页爬取

在使用过Python的scrapy、pyspider，Java的webmagic爬虫框架，发现scrapy、webmagic均对所有url统一处理。
需要在解析逻辑中去判断是商品信息url还是详情页url，然后编写不同的解析逻辑。

相反pyspider则通过callback完美的解决了这个问题。

因此，有了boy项目。