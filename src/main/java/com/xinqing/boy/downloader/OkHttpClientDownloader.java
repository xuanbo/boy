package com.xinqing.boy.downloader;

import com.xinqing.boy.Request;
import com.xinqing.boy.Response;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuan
 * @since 1.0.0
 */
public class OkHttpClientDownloader implements Downloader {
    
    private static final Logger LOG = LoggerFactory.getLogger(OkHttpClientDownloader.class);

    private OkHttpClient client = buildOkHttpClient();

    private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

    public OkHttpClientDownloader() {
    }

    public OkHttpClientDownloader(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Response download(Request request) {
        Call call = client.newCall(wrapperRequest(request));
        Response response = null;
        try {
            okhttp3.Response resp = call.execute();
            response = handleResponse(resp);
        } catch (IOException e) {
            LOG.warn("request {} error", request.getUrl(), e);
        }
        return response == null ? Response.fail() : response;
    }

    private OkHttpClient buildOkHttpClient() {
        return new OkHttpClient().newBuilder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                }).build();
    }

    private okhttp3.Request wrapperRequest(Request request) {
        if (!Request.GET.equals(request.getMethod())) {
            throw new IllegalStateException("only `GET` support.");
        }
        return new okhttp3.Request.Builder()
                .headers(getHeaders(request))
                .url(request.getUrl())
                .build();
    }

    private Headers getHeaders(Request request) {
        return Headers.of(request.getHeaders());
    }

    private Response handleResponse(okhttp3.Response resp) throws IOException {
        Response response = new Response();
        response.setStatusCode(resp.code());
        response.setHeaders(resp.headers().toMultimap());
        ResponseBody body = resp.body();
        if (body != null) {
            byte[] bytes = body.bytes();
            response.setRawBytes(bytes);
            response.setRawText(new String(bytes));
        }
        return response;
    }
}
