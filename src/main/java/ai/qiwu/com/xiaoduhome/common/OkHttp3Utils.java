package ai.qiwu.com.xiaoduhome.common;

import ai.qiwu.com.xiaoduhome.pojo.AudioBoxData;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 苗权威
 * @dateTime 19-7-18 上午10:54
 */
@Slf4j
public class OkHttp3Utils {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public static String doGet(String url) throws IOException {
        return doGet(url, new HashMap<>());
    }

    public static String doGet(String url, Map<String, String> params) throws IOException {
        return doGet(url, params, new HashMap<>());
    }

    public static String doGet(String url, Map<String, String> params, Map<String, String> header) throws IOException {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();

        HttpUrl.Builder urlBuild = request.url().newBuilder();
        if (!CollectionUtils.isEmpty(params)) {
            Iterator<Map.Entry<String, String>> paramsIterator = params.entrySet().iterator();
            paramsIterator.forEachRemaining(e -> urlBuild.addQueryParameter(e.getKey(), e.getValue()));
        }
        Headers.Builder headerBuilder = request.headers().newBuilder();
        if (!CollectionUtils.isEmpty(header)) {
            Iterator<Map.Entry<String, String>> headIterator = header.entrySet().iterator();
            headIterator.forEachRemaining(e -> headerBuilder.add(e.getKey(), e.getValue()));
        }

        builder.url(urlBuild.build()).headers(headerBuilder.build());
        Response response = client.newCall(builder.build()).execute();
        try (ResponseBody body = response.body()) {
            return body.string();
        }
    }

    public static String doPost(String url, Map<String, String> params) throws IOException {
        return doPost(url, new HashMap<>(), params,null);
    }

    public static String doPost(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        return doPost(url, headers, params, null);
    }

    public static String doPost(String url, File file) throws IOException {
        return doPost(url , new HashMap<>(), new HashMap<>(), file);
    }

    public static String doPostJsonStr(String url, String requestJsonStr) throws IOException {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();

        Headers.Builder headBuilder = request.headers().newBuilder();
        headBuilder.add("Content-Type", "application/json;charset=utf-8");
//        headBuilder.add("App-Channel-Id", "jiaoyou-audio-test");

//        Iterator<Map.Entry<String, String>> headIterator = headers.entrySet().iterator();
//        headIterator.forEachRemaining(e -> headBuilder.add(e.getKey(), e.getValue()));

        builder.headers(headBuilder.build()).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJsonStr));
        Response response = client.newCall(builder.build()).execute();
        try (ResponseBody body = response.body()){
            return body.string();
        }catch (SocketTimeoutException e) {
            log.error("捕获到SocketTimeoutException:{}", e.toString());
            client.dispatcher().cancelAll();
            client.connectionPool().evictAll();
        }
        return null;
    }

    public static String doPostJsonStr(String url, String requestJsonStr, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();

        Headers.Builder headBuilder = request.headers().newBuilder();

        Iterator<Map.Entry<String, String>> headIterator = headers.entrySet().iterator();
        headIterator.forEachRemaining(e -> headBuilder.add(e.getKey(), e.getValue()));

        builder.headers(headBuilder.build()).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJsonStr));
        Response response = client.newCall(builder.build()).execute();
        try (ResponseBody body = response.body()){
            return body.string();
        }catch (SocketTimeoutException e) {
            log.error("捕获到SocketTimeoutException:{}", e.toString());
            client.dispatcher().cancelAll();
            client.connectionPool().evictAll();
        }
        return null;
    }

    public static String doPostJsonStrForCentralWithUidParam(String url, String requestJsonStr, Map<String, String> headers,
                                                             String uid) throws IOException {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();
        HttpUrl.Builder urlBuild = request.url().newBuilder();
        Headers.Builder headBuilder = request.headers().newBuilder();

        Iterator<Map.Entry<String, String>> headIterator = headers.entrySet().iterator();
        headIterator.forEachRemaining(e -> headBuilder.add(e.getKey(), e.getValue()));

        urlBuild.addQueryParameter("uid", uid);

        builder.headers(headBuilder.build()).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJsonStr));
        Response response = client.newCall(builder.build()).execute();
        try (ResponseBody body = response.body()){
            return body.string();
        }catch (SocketTimeoutException e) {
            log.error("捕获到SocketTimeoutException:{}", e.toString());
            client.dispatcher().cancelAll();
            client.connectionPool().evictAll();
        } catch (Exception e) {
            log.error("doPostJsonStrForCentralWithUidParam error:"+e);
        }
        return null;
    }

    public static void asyncPost(String url, String strJson, final AudioBoxData data) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), strJson))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("异步请求失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()){
                    data.setPath(body.string());
                    log.info("异步请求成功");
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        });
    }

    public static void noticeAsync(String url, String type, String value) {
        url = new StringBuilder(url).append("?").append("type=").append(type).append("&value=").append(value).toString();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("退出失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()){
                    body.string();
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        });
    }

    public static void backPostAsync(String url, String requestJsonStr, Integer type) {
        String header = "jiaoyou-audio-test";
        if (type != null) {
            switch (type) {
                case 1 :
                case 3 : header = "jiaoyou-audio-child-test";break;
                case 2 :
                case 4 : header = "jiaoyou-audio-adult-test";break;
                case 6 : header = "baidu-screen-jiaoyou-audio-test";break;
                default: header = "jiaoyou-audio-test";
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJsonStr))
                .header("App-Channel-Id", header)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("退出失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()){
                    body.string();
//                    log.info("退出成功:{}", body.string());
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error("backPostAsync:"+e.toString());
                }
            }
        });
    }

    public static void backPostAsyncByChannel(String url, String requestJsonStr, String channel) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJsonStr))
                .header("App-Channel-Id", channel)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("退出失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()){
                    body.string();
//                    log.info("退出成功:{}", body.string());
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error("backPostAsync:"+e.toString());
                }
            }
        });
    }

    public static void xiaoaiBackPostAsync(String url, String requestJsonStr, Integer type) {
        String header = "xiaoai-jiaoyou-audio-test";
        if (type != null) {
            switch (type) {
                case 1 :
                case 3 : header = "xiaoai-jiaoyou-audio-child-test";break;
                case 2 :
                case 4 : header = "xiaoai-jiaoyou-audio-adult-test";break;
                default: header = "xiaoai-jiaoyou-audio-test";
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJsonStr))
                .header("App-Channel-Id", header)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("退出失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()){
                    body.string();
//                    log.info("退出成功:{}", body.string());
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error("xiaoaiBackPostAsync:"+e.toString());
                }
            }
        });
    }

    public static void serverChangeCallOtherSalve(String url, String data) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("通知从节点切换地址失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (ResponseBody body = response.body()){
                    log.info("关闭从节点服务器迁移状态码成功:{}",body.string());
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error("serverChangeCallOtherSalve:"+e.toString());
                }
            }
        });
    }

    public static void tmallBackPostAsync(String url, String requestJsonStr, Integer type) {
        String header = "tianmao-jiaoyou-audio-adult-test";
        if (type != null) {
            switch (type) {
                case 2:
                case 4: header = "tianmao-jiaoyou-audio-child-test";break;
                case 3:
                case 5: header= "tianmao-jiaoyou-audio-adult-test";break;
                default: header = "tianmao-jiaoyou-audio-test";
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJsonStr))
                .header("App-Channel-Id", header)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("退出失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()){
                    body.string();
//                    log.info("退出成功:{}", body.string());
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        });
    }

    public static void postAsync(String url, String requestJsonStr, String prefixLog) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestJsonStr,MediaType.parse("application/json; charset=utf-8")))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.warn(prefixLog+" 失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                log.info(prefixLog+" 成功");
                try (ResponseBody body = response.body()){
                    body.string();
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        });
    }

    public static String doPost(String url, final Map<String, String> headers, final Map<String, String> params, File file) throws IOException {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();

        Headers.Builder headBuilder = request.headers().newBuilder();

        Iterator<Map.Entry<String, String>> headIterator = headers.entrySet().iterator();
        headIterator.forEachRemaining(e -> headBuilder.add(e.getKey(), e.getValue()));

        MultipartBody.Builder requestBuilder = new MultipartBody.Builder();
        System.out.println(params);
        params.forEach(requestBuilder::addFormDataPart);

        if (file != null) {
            requestBuilder.addFormDataPart("file", file.getName(),
                    RequestBody.create(MediaType.parse("audio/mpeg"), file));
        }
        builder.headers(headBuilder.build()).post(requestBuilder.build());
        Response response = client.newCall(builder.build()).execute();
        try (ResponseBody body = response.body()){
            return body.string();
        }
    }



    public static String doPost(String url, Map<String, String> headers, Map<String, String> params,
                                String fileName, byte[] fileByte) throws IOException {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();

        Headers.Builder headBuilder = request.headers().newBuilder();

        Iterator<Map.Entry<String, String>> headIterator = headers.entrySet().iterator();
        headIterator.forEachRemaining(e -> headBuilder.add(e.getKey(), e.getValue()));

        MultipartBody.Builder requestBuilder = new MultipartBody.Builder();
        Iterator<Map.Entry<String, String>> paramsIterator = params.entrySet().iterator();
        paramsIterator.forEachRemaining(e -> requestBuilder.addFormDataPart(e.getKey(), e.getValue()));

        if (fileByte.length > 0) {
            requestBuilder.addFormDataPart("uploadFiles", fileName,
                    RequestBody.create(MediaType.parse("application/octet-stream"), fileByte));
        }
        builder.headers(headBuilder.build()).post(requestBuilder.build());
        Response response = client.newCall(builder.build()).execute();
        try (ResponseBody body = response.body()){
            return body.string();
        }
    }

    public static String doPut(String url, Map<String, String> headers, String jsonStr) throws IOException {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();

        Headers.Builder headBuilder = request.headers().newBuilder();

        Iterator<Map.Entry<String, String>> headIterator = headers.entrySet().iterator();
        headIterator.forEachRemaining(e -> headBuilder.add(e.getKey(), e.getValue()));

        builder.headers(headBuilder.build()).put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr));
        Response response = client.newCall(builder.build()).execute();
        try (ResponseBody body = response.body()){
            return body.string();
        }
    }

    public static void doPutAsy(String url, String requestJsonStr) {
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(requestJsonStr,MediaType.parse("application/json; charset=utf-8")))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("天猫精灵发送无音频数据失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()){
                    body.string();
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        });
    }

    public static void doPostAsy(String url, String requestJsonStr) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestJsonStr,MediaType.parse("application/json; charset=utf-8")))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("doPostAsy fail,"+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()){
                    body.string();
                    log.info("doPostAsy success");
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        });
    }

    public static void doDelAsy(String url, String requestJsonStr) {
        Request request = new Request.Builder()
                .url(url)
                .delete(RequestBody.create(requestJsonStr,MediaType.parse("application/json; charset=utf-8")))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("doDelAsy fail,"+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()){
                    body.string();
                    log.info("doDelAsy success");
                } catch (SocketTimeoutException e) {
                    log.error("捕获到SocketTimeoutException:{}", e.toString());
                    client.dispatcher().cancelAll();
                    client.connectionPool().evictAll();
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        });
    }

    public static String doDelete(String url, Map<String, String> headers, String jsonStr) throws IOException {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();

        Headers.Builder headBuilder = request.headers().newBuilder();

        Iterator<Map.Entry<String, String>> headIterator = headers.entrySet().iterator();
        headIterator.forEachRemaining(e -> headBuilder.add(e.getKey(), e.getValue()));

        builder.headers(headBuilder.build()).delete(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr));
        Response response = client.newCall(builder.build()).execute();
        try (ResponseBody body = response.body()){
            return body.string();
        }
    }

    public static void main(String[] args) {
        String url = "http://192.168.1.82:8081/bot/condition";
        String authorization = "Basic ZXlKaGJHY2lPaUpJVXpVeE1pSXNJbWxoZENJNk1UVTJPRFU1T0RBM05Dd2laWGh3SWpveE5UWTROalE0TkRjMGZRLmV5SnBaQ0k2TXpZc0ltRjFkR2h2Y21sMGVTSTZNWDAueDdDY2Zhak12ZmRkbExwUFVZYXFkUmZVNF91S2xBX0Q5NzZTaGZTQWUzLWh6SVg0Wm91bFdkNkpvS1pRQ0VlQ0N5b3k0dXRLRzJYVDhFZG9aZHJyU2c6czAwcGVyczNjcmV0";

    }
}
