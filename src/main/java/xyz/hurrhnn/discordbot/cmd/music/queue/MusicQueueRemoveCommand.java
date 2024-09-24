package xyz.hurrhnn.discordbot.cmd.music.queue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;
import xyz.hurrhnn.discordbot.cmd.music.GuildMusicManager;
import xyz.hurrhnn.discordbot.cmd.music.PlayerManager;

import java.util.Collections;
import java.util.List;

public class MusicQueueRemoveCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getMusicManager(cmdContext.getGuild());
        AudioPlayer player = musicManager.audioPlayer;

        if (isArgsEmpty(cmdContext.getArgs())) {
            cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Queue", this.getHelp()).build()).queue();
            return;
        }

        try {
            int arg = Integer.parseInt(cmdContext.getArgs().get(0));

            if (player.getPlayingTrack() == null) {
                cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Queue", "```E: There are no songs in the queue to remove.```").build()).queue();
                return;
            }

            if (arg == 0) {
                cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Queue", "```E: Please use !!music skip.```").build()).queue();
                return;
            }

            Object[] str = musicManager.scheduler.getQueue().toArray();
            for (int i = 0; i < musicManager.scheduler.getQueue().size(); i++) {
                AudioTrack audioTrack = (AudioTrack) str[i];
                if (arg == (i + 1)) {
                    musicManager.scheduler.getQueue().remove(audioTrack);
                    cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Queue", "Queue number `" + (i + 1) + "` has been removed.").build()).queue();
                    return;
                }
            }
        } catch (NumberFormatException ignored) {
            cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Queue", "```E: Couldn't remove the queue. Make sure the queue number is correct!```").build()).queue();
        }
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music queue remove [Queue Number]\n" +
                "-- Removes music from the queue.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("r");
    }

    @Override
    public boolean isArgsEmpty(List<String> args) {
        return args.size() == 0;
    }
}
