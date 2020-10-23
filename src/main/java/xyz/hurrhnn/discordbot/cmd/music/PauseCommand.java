package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

public class PauseCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(cmdContext.getGuild());

        if (!musicManager.scheduler.player.isPaused()) {
            musicManager.scheduler.player.setPaused(true);
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - pause!", "Pause the Player.").build()).queue();
        } else cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - pause!", "```E: Player is already paused.```").build()).queue();
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getHelp() {
        return null;
    }
}
