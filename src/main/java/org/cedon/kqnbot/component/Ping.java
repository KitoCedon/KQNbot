package org.cedon.kqnbot.component;

import java.util.regex.Matcher;

import org.springframework.stereotype.Component;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

@Shiro
@Component
public class Ping {
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^\\$ping$")
    public void Handler(Bot bot, AnyMessageEvent event, Matcher matcher) {

        bot.sendMsg(event, "还活着喵~", false);
    }
}
