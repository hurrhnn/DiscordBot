package xyz.hurrhnn.discordbot.cmd.music;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.hurrhnn.discordbot.Main;
import xyz.hurrhnn.discordbot.util.HTTPMethods;
import xyz.hurrhnn.discordbot.util.SQL;

import javax.annotation.Nullable;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class PlayCommander {
    public final YouTube youTube;

    public PlayCommander(List<String> args, MessageReceivedEvent event, String mp3Name) {
        YouTube temp = null;
        try {
            temp = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null
            )
                    .setApplicationName("TARS-Alpha")
                    .build();
        } catch (Exception ignored) {
        }
        youTube = temp;
        handle(args, event, mp3Name);
    }

    public void handle(List<String> args, MessageReceivedEvent event, String mp3Name) {

        TextChannel channel = event.getChannel().asTextChannel();
        StringBuilder input = new StringBuilder();

        for (int i = 0; i < args.toArray().length; i++)
            input.append(args.toArray()[i]).append(" ");

        if (mp3Name != null) {
            if(!mp3Name.contains(".mp3")) mp3Name += ".mp3";
            if(HTTPMethods.GET("https://hurrhnn.xyz/downloadMP3fromDB.php?mp3=" + mp3Name, null).contains("ERROR")) {
                channel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Play!", "```E: No mp3 results were found on mp3 list.```").build()).queue();
                return;
            }
            input = new StringBuilder().append("https://hurrhnn.xyz/MP3/").append(mp3Name);
        } else {
            if (!isURL(input.toString().trim())) {
                String ytSearched = searchYoutube(input.toString(), event);
                if (ytSearched == null) {
                    channel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Play!", "```E: No results were found on YouTube.```").build()).queue();
                    return;
                }
                input = new StringBuilder(ytSearched);
            }
        }
        PlayerManager manager = PlayerManager.getInstance();
        manager.loadAndPlay(event.getChannel().asTextChannel(), input.toString().trim(), mp3Name);
    }

    private boolean isURL(String URL) {
        try {
            new URL(URL);
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    @Nullable
    public String searchYoutube(String input, MessageReceivedEvent event) {
        try {
            List<SearchResult> results = youTube.search()
                    .list("id,snippet")
                    .setQ(input)
                    .setMaxResults(1L)
                    .setType("video")
                    .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                    .setKey(Objects.requireNonNull(SQL.getSQLData(Main.con, "info", "token", event))[1])
                    .execute()
                    .getItems();

            if (!results.isEmpty()) {
                String videoId = results.get(0).getId().getVideoId();
                return "https://www.youtube.com/watch?v=" + videoId;
            }
        } catch (Exception e) {
            PrintStream errPrintStream = null;
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            errPrintStream = new PrintStream(err, true, StandardCharsets.UTF_8);
            e.printStackTrace(errPrintStream);
            event.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + "E: " + err.toString().split("\n")[0] + "\n```").build()).queue();

        }
        return null;
    }
}
