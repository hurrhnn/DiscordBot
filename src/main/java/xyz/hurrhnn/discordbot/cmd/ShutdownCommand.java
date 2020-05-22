package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.BotCommons;
import org.slf4j.LoggerFactory;
import xyz.hurrhnn.discordbot.EventListener;

public class ShutdownCommand implements ICmd{
    @Override
    public void handle(CmdContext cmdContext) {
        cmdContext.getChannel().sendMessage("Performing Shutting down...").queue();
        LoggerFactory.getLogger(EventListener.class).info("Shutting down...");

        cmdContext.getJDA().shutdown();
        BotCommons.shutdown(cmdContext.getJDA());
    }

    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public String getHelp() {
        return "Shutting down a bot. (Everyone Only)";
    }
}
