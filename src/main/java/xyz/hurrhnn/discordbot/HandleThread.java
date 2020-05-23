package xyz.hurrhnn.discordbot;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.hurrhnn.discordbot.cmd.CommandManager;

public class HandleThread extends Thread {

    private final GuildMessageReceivedEvent event;
    private final CommandManager manager = new CommandManager();

    public HandleThread(GuildMessageReceivedEvent event)
    { this.event = event; }

    public void run()
    { manager.handle(event); }
}