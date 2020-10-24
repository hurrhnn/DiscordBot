package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;

public class LoopCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getMusicManager(cmdContext.getGuild());
        musicManager.scheduler.isLoopQueue = !musicManager.scheduler.isLoopQueue;

        cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - loop!", "The loop mode of the music player has been " + (musicManager.scheduler.isLoopQueue ? "activated." : "deactivated.")).build()).queue();
    }

    @Override
    public String getName() {
        return "loop";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music loop\n" +
                "-- Toggle the loop mode of the music player.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("lo");
    }
}
