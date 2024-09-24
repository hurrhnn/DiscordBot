package xyz.hurrhnn.discordbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.hurrhnn.discordbot.cmd.CommandManager;

public class HandleThread extends Thread {

    private final MessageReceivedEvent event;
    private final CommandManager manager = new CommandManager();

    public HandleThread(MessageReceivedEvent event)
    { this.event = event; }

    public void run()
    { manager.handle(event); }
}