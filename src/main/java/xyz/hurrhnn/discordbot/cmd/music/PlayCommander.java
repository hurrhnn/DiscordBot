package xyz.hurrhnn.discordbot.cmd.music;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.hurrhnn.discordbot.util.SQL;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PlayCommander {
    public final YouTube youTube;

    public PlayCommander(List<String> args, GuildMessageReceivedEvent event, String mp3Name) {
        YouTube temp = null;

        try {
            temp = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null
            )
                    .setApplicationName("JDA4 Discord Bot")
                    .build();
        } catch (Exception ignored) { }
        youTube = temp;
        handle(args, event, mp3Name);
    }

    public void handle(List<String> args, GuildMessageReceivedEvent event, String mp3Name) {

        TextChannel channel = event.getChannel();
        StringBuilder input = new StringBuilder();

        for(int i = 0; i < args.toArray().length; i++)
            input.append(args.toArray()[i]).append(" ");

        if(!isURL(input.toString().trim()))
        {
            String ytSearched = searchYoutube(input.toString(), event);
            if (ytSearched == null) {
                channel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - play!", "```E: No results were found on YouTube.```").build()).queue();
                return;
            }
            input = new StringBuilder(ytSearched);
        }
        PlayerManager manager = PlayerManager.getInstance();
        manager.loadAndPlay(event.getChannel(), input.toString().trim(), mp3Name);
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
            event.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + "E: " + err.toString().split("\n")[0] + "\n```").build()).queue();
        }
        return null;
    }
}