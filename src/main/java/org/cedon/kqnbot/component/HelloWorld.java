package org.cedon.kqnbot.component;

import org.springframework.stereotype.Component;

import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;

import java.util.regex.Matcher;

@Shiro
@Component
public class HelloWorld {
    @PrivateMessageHandler
    @MessageHandlerFilter(cmd = "^/echo\s(.*)?")
    public void echo(Bot bot, PrivateMessageEvent event, Matcher matcher) {
        System.out.println(matcher);
        bot.sendPrivateMsg(event.getUserId(), matcher.group(1), false);
    }
}
