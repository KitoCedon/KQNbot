package org.cedon.kqnbot.command;

import org.springframework.stereotype.Component;

import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

import java.util.regex.Matcher;

@Shiro
@Component
public class Echo {
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^\\$echo(?:\s(.*))?$")
    public void echo(Bot bot, AnyMessageEvent event, Matcher matcher) {

        String msg = matcher.group(1);
        if (msg == null || msg.trim().isEmpty()) {
            bot.sendMsg(event, "缺少参数, 请使用格式: $echo <内容>", false);
            return;
        }

        bot.sendMsg(event, msg, false);
    }
}
