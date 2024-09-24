package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;

public class StopCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(cmdContext.getGuild());
        musicManager.scheduler.isLoopQueue = false;
        musicManager.scheduler.getQueue().clear();
        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.player.setPaused(false);
        cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Stop", "Stop the music and clear the music queue.").build()).queue();
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music stop\n" +
                "-- Stop the music and clear the music queue..\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("st");
    }
}
