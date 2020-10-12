package xyz.hurrhnn.discordbot.cmd.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        this.audioPlayerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        this.audioPlayerManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

        public void loadAndPlay(TextChannel channel, String trackUrl, String ytThumbnail, String mp3Name) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if(mp3Name != null) channel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - play!", "\"" + mp3Name + "\"\nwas successfully added to queue.").build()).queue();
                else channel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - play!", "[" + track.getInfo().title + "](" + trackUrl + ")\nadded to queue.").setThumbnail(ytThumbnail).build()).queue();
                musicManager.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                //channel.sendMessage("\"" + firstTrack.getInfo().title + "\"이 대기열에 추가됨!" + " (첫 번째 곡 : " + playlist.getName() + ")").queue();
                channel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - play!", "[" + playlist.getName() + "](" + playlist.getTracks().get(0).getInfo().uri + ")\nadded to queue.").setThumbnail("https://i.ytimg.com/vi/" + trackUrl.substring(trackUrl.trim().indexOf("watch?v="), trackUrl.trim().indexOf('&')).replace("watch?v=", "") + "/0.jpg").build()).queue();

                for (final AudioTrack track : tracks) musicManager.scheduler.queue(track);
            }

            @Override 
            public void noMatches() {
                channel.sendMessage("영상을 찾을 수 없어요.. " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("재생 불가: " + exception.getMessage()).queue();
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}