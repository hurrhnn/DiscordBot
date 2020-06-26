package xyz.hurrhnn.discordbot;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hurrhnn.discordbot.util.Info;
import xyz.hurrhnn.discordbot.util.LogThread;

import javax.annotation.Nonnull;
import java.io.File;

public class EventListener extends ListenerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} Bot version {} Started! ", event.getJDA().getSelfUser().getName(), Info.getVersion());
        new Thread(() -> {
            while (true){
                try {
                    event.getJDA().getPresence().setActivity(Activity.playing("?prefix"));
                    Thread.sleep(5000);
                    event.getJDA().getPresence().setActivity(Activity.watching("鬼滅の刃"));
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) { }
            }
        }).start();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();
        if (user.isBot() || event.isWebhookMessage()) return;
        String raw = event.getMessage().getContentRaw();

        if(raw.contains("<@!345473282654470146>") || raw.contains("<@345473282654470146>")) event.getChannel().sendFile(new File("isOwnerMentioned.png")).queue();
        if(raw.contains("@everyone")) event.getChannel().sendFile(new File("isEveryoneMentioned.png")).queue();
        Thread logThread = new LogThread(event, LOGGER);
        logThread.setName("LogThread-" + ++Info.logThreadCount);
        logThread.start();

        if (raw.startsWith("?") || raw.startsWith(Info.getPrefix(event))) {
            Thread handleThread = new HandleThread(event);
            handleThread.start();
        }
    }
}
