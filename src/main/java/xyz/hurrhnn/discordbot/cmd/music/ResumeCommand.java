package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

public class ResumeCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(cmdContext.getGuild());
        if (musicManager.scheduler.player.isPaused()) {
            musicManager.scheduler.player.setPaused(false);
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - Resume", "Resume the player.").build()).queue();
        } else cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - Resume", "Player is already playing.").build()).queue();
    }

    @Override
    public String getName() {
        return "resume";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music resume\n" +
                "-- Resume the music player.\n```";
    }
}
