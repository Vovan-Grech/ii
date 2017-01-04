package org.ayfaar.app.spring.listeners;


import com.pushbullet.Builder;
import com.pushbullet.PushbulletClient;
import org.ayfaar.app.event.PushEvent;
import org.ayfaar.app.event.HasUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static com.pushbullet.Builder.pushbullet;

@Component
public class NotificationListener implements ApplicationListener<PushEvent> {

    @Autowired
    private PushbulletClient pushbulletClient;
    @Value("${pushbullet.channel}")
    private String channel;
    @Value("${pushbullet.key}")
    private String key;
    @Autowired
    private ApplicationContext ctx;

    @Override
    public void onApplicationEvent(final PushEvent event) {

        if (ctx.getParent()!=null) return; // fix to avoid duplications

        if (key == null || key.isEmpty()) {
            // todo пишем в лог что за ивент
            return;
        }

        new Thread(() -> {
            final Builder.PushesBuilder pusher = pushbullet(pushbulletClient).pushes().channel(channel);
            if(event instanceof HasUrl){
                pusher.link(event.getTitle(), event.getMessage(), ((HasUrl) event).getUrl());
            }else {
                pusher.note(event.getTitle(), event.getMessage());
            }
        }).start();
    }
}
