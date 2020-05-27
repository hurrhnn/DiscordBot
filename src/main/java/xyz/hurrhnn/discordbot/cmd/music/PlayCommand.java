package xyz.hurrhnn.discordbot.cmd.music;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import xyz.hurrhnn.discordbot.util.SQL;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PlayCommand {
    public final YouTube youTube;

    public PlayCommand(String[] args, GuildMessageReceivedEvent event, AudioManager audioManager, String mp3Name) {
        YouTube temp = null;

        try {
            temp = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null
            )
                    .setApplicationName("ZENITSU Discord Bot")
                    .build();
        } catch (Exception ignored) { }
        youTube = temp;
        handle(args, event, audioManager, mp3Name);
    }

    public void handle(String[] args, GuildMessageReceivedEvent event, AudioManager audioManager, String mp3Name) {

        TextChannel channel = event.getChannel();

        if (args.length < 3) {
            channel.sendMessage("링크나 동영상 이름을 입력해주세요.").queue();
            return;
        }

        if (!audioManager.isConnected()) {
            channel.sendMessage("봇이 음성 채널에 연결되어 있지 않습니다.").queue();
            return;
        }

        StringBuilder input = new StringBuilder();

        for (int i = 2; i < args.length; i++) input.append(args[i]).append(" ");

        if (!isUrl(input.toString())) {
            String ytSearched = searchYoutube(input.toString(), event);

            if (ytSearched == null) {
                channel.sendMessage("검색된 결과가 없습니다.").queue();
                return;
            }
            input = new StringBuilder(ytSearched);
        }

        PlayerManager manager = PlayerManager.getInstance();

        manager.loadAndPlay(event.getChannel(), input.toString().trim(), mp3Name);
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    @Nullable
    public String searchYoutube(String input, GuildMessageReceivedEvent event) {

        try {
            List<SearchResult> results = youTube.search()
                    .list("id,snippet")
                    .setQ(input)
                    .setMaxResults(1L)
                    .setType("video")
                    .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                    .setKey(SQL.getSQLData(SQL.initSQLConnection("discordjavabot"), "info", "token", event)[1])
                    .execute()
                    .getItems();

            if (!results.isEmpty()) {
                String videoId = results.get(0).getId().getVideoId();
                return "https://www.youtube.com/watch?v=" + videoId;
            }
        } catch (Exception e) {
            PrintStream errPrintStream = null;
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            try {
                errPrintStream = new PrintStream(err, true, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException ignored) { }
            e.printStackTrace(errPrintStream);
            event.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + err.toString().split("\n")[0] + "\n```").build()).queue();
        }
        return null;
    }
}