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
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(cmdContext.getGuild());

        if (musicManager.player.getPlayingTrack() == null) {
            cmdContext.getChannel().sendMessage("아무 것도 재생 되어 있지 않습니다.").queue();
            return;
        }
        AudioTrackInfo info = musicManager.player.getPlayingTrack().getInfo();

        cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle(!musicManager.player.isPaused() ? "재생 중: [" + info.title + "]" : "일시 정지됨: [" + info.title + "]", musicManager.player.isPaused() ? formatTime(musicManager.player.getPlayingTrack().getPosition()) + " \u23F8 " + formatTime(musicManager.player.getPlayingTrack().getDuration()) : formatTime(musicManager.player.getPlayingTrack().getPosition()) + " ▶ " + formatTime(musicManager.player.getPlayingTrack().getDuration())).build()).queue();
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
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("np");
    }
}
