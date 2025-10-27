package org.cedon.kqnbot.command;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.boot.system.ApplicationHome;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

import cn.hutool.http.HttpUtil;

import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.w3c.dom.Document;
import org.cedon.kqnbot.properties.McsmProperties;
import org.cedon.kqnbot.util.URLUtils;;

@Shiro
@Component
public class Info {

    private static final Logger logger = LoggerFactory.getLogger(Info.class);

    private final McsmProperties mcsmProperties;

    public Info(McsmProperties mcsmProperties) {
        this.mcsmProperties = mcsmProperties;
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^\\$info(?:\s(.*))?$")
    public void handler(Bot bot, AnyMessageEvent event, Matcher matcher) {

        Map<String, String> instances = mcsmProperties.getInstances();

        String instanceName = matcher.group(1);
        if (instanceName == null || instanceName.trim().isEmpty()) {
            bot.sendMsg(event, "缺少参数, 请使用格式: $info <实例名>", false);
            return;
        }

        instanceName = instanceName.toLowerCase();
        logger.info("获取到实例参数: {}", instanceName);

        String uuid = instances.get(instanceName);

        if (uuid == null) {
            bot.sendMsg(event, "未找到对应的实例配置: " + instanceName, false);
            return;
        }

        try {
            String instanceInfo = getInstanceInfo(
                    mcsmProperties.getUrl(),
                    mcsmProperties.getApikey(),
                    uuid,
                    mcsmProperties.getDaemonId());
            bot.sendMsg(event, instanceInfo, false);
        } catch (Exception e) {
            logger.error("获取实例信息失败: {}", e.getMessage(), e);
            bot.sendMsg(event, "获取实例信息失败,可能是实例不存在,请稍后再试。", false);
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
    private String getInstanceInfo(String url, String apikey, String uuid, String daemonId) {

        String instanceInfoPattern = """
                服务器名称:{0}
                服务器状态:{1}
                在线人数:{2}/{3}
                MineCraft版本:{4}
                获取时间:{5}""";
        /**
         * 构建URL并发送GET请求, 获取JSON格式res
         */
        String URLofInstance = URLUtils.buildURLofInstance(url, apikey, uuid, daemonId);
        logger.info("{}", URLofInstance);

        /**
         * Convert JSON to Java Object
         */
        JSONObject res = JSON.parseObject(HttpUtil.get(URLofInstance));

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

    private void renderImg() {
        File path = new File(this.getClass().getResource("/").getPath());
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        // String url = "g"
    }
}
