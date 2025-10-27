package org.cedon.kqnbot.util;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.CharsetUtil;

public class URLUtils {
    // TODO: 从全局变量读取 url, apikey
    private static String pathOfInstance = "/api/instance";

    public static String buildURLofInstance(String url, String apikey, String uuid, String daemonId) {

        UrlBuilder buildUrl = UrlBuilder.ofHttp(url, CharsetUtil.CHARSET_UTF_8);
        buildUrl.setScheme("https")
                .addPath(pathOfInstance)
                .addQuery("apikey", apikey)
                .addQuery("uuid", uuid)
                .addQuery("daemonId", daemonId);

        return buildUrl.build();
    }
}
