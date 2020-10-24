package xyz.hurrhnn.discordbot.cmd.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NowPlayingCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getMusicManager(cmdContext.getGuild());

        if (musicManager.scheduler.player.getPlayingTrack() == null) {
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - nowPlaying", "```E: Nothing is playing.```").build()).queue();
            return;
        }
        AudioTrackInfo info = musicManager.scheduler.player.getPlayingTrack().getInfo();

        cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle(!musicManager.scheduler.player.isPaused() ? "Now Playing: [" + info.title + "]" : "Paused: [" + info.title + "]", musicManager.scheduler.player.isPaused() ? formatTime(musicManager.scheduler.player.getPlayingTrack().getPosition()) + " \u23F8 " + formatTime(musicManager.scheduler.player.getPlayingTrack().getDuration()) : formatTime(musicManager.scheduler.player.getPlayingTrack().getPosition()) + " ▶ " + formatTime(musicManager.scheduler.player.getPlayingTrack().getDuration())).build()).queue();
    }

    public String formatTime(long timeInMillis) {
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, (minutes > 59) ? minutes % 60 : minutes, seconds);
    }

    @Override
    public String getName() {
        return "nowPlaying";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music nowPlaying\n" +
                "-- Displays the music information currently playing.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("np");
    }
}
