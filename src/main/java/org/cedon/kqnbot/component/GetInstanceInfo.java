package org.cedon.kqnbot.component;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
// import com.alibaba.fastjson2.JSONWriter;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;

import cn.hutool.http.HttpUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.CharsetUtil;

import org.cedon.kqnbot.properties.McsmProperties;

@Shiro
@Component
public class GetInstanceInfo {

    private static final Logger logger = LoggerFactory.getLogger(GetInstanceInfo.class);

    private final McsmProperties mcsmProperties;

    private String path = "/api/instance";

    public GetInstanceInfo(McsmProperties mcsmProperties) {
        this.mcsmProperties = mcsmProperties;
    }

    @PrivateMessageHandler
    @MessageHandlerFilter(cmd = "^/info\s(.*)?")
    public void handler(Bot bot, PrivateMessageEvent event, Matcher matcher) {

        String instanceName = matcher.group(1);
        logger.info("获取到实例参数: {}", instanceName);

        Map<String, String> instances = mcsmProperties.getInstances();
        String uuid = instances.get(instanceName);

        if (uuid == null) {
            bot.sendPrivateMsg(event.getUserId(), "未找到对应的实例配置: " + instanceName, false);
            return;
        }

        try {
            String instanceInfo = getInstanceInfo(
                    mcsmProperties.getUrl(),
                    mcsmProperties.getApikey(),
                    uuid,
                    mcsmProperties.getDaemonId());
            bot.sendPrivateMsg(event.getUserId(), instanceInfo, false);
        } catch (Exception e) {
            logger.error("获取实例信息失败: {}", e.getMessage(), e);
            bot.sendPrivateMsg(event.getUserId(), "获取实例信息失败，请稍后再试。", false);
        }
    }

    /**
     * 获取服务器基本信息, 在线状态以及玩家人数
     * 
     * @param url      管理地址
     * @param apikey   api密钥
     * @param uuid     服务器实例ID
     * @param daemonId 守护进程ID
     * @return 服务器当前基本信息
     */
    public String getInstanceInfo(String url, String apikey, String uuid, String daemonId) {

        String instanceInfoPattern = """
                服务器名称:{0}
                服务器状态:{1}
                在线人数:{2}/{3}
                MineCraft版本:{4}
                获取时间:{5}""";
        /**
         * 构建URL并发送GET请求, 获取JSON格式res
         */
        UrlBuilder buildUrl = UrlBuilder.ofHttp(url, CharsetUtil.CHARSET_UTF_8);
        buildUrl.setScheme("https")
                .addPath(path)
                .addQuery("apikey", apikey)
                .addQuery("uuid", uuid)
                .addQuery("daemonId", daemonId)
                .build();
        logger.info("{}", buildUrl.toString());

        /**
         * Convert JSON to Java Object
         */
        JSONObject res = JSON.parseObject(HttpUtil.get(buildUrl.toString()));
        // String formattedJson = JSON.toJSONString(res,
        // JSONWriter.Feature.PrettyFormat);
        // logger.info("获取到的JSON文件为:\n{}", formattedJson);

        String isOnline;

        JSONObject data = res.getJSONObject("data");
        JSONObject config = data.getJSONObject("config");
        JSONObject info = data.getJSONObject("info");
        String nickname = config.getString("nickname");
        Boolean mcPingOnline = info.getBooleanValue("mcPingOnline");
        if (mcPingOnline == true) {
            isOnline = "Online";
        } else {
            isOnline = "Offline";
        }

        String currentPlayers = info.getString("currentPlayers");
        String maxPlayers = info.getString("maxPlayers");
        String version = info.getString("version");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String instanceInfo = MessageFormat.format(
                instanceInfoPattern,
                nickname,
                isOnline,
                currentPlayers,
                maxPlayers,
                version,
                time);
        return instanceInfo;
    }
}
