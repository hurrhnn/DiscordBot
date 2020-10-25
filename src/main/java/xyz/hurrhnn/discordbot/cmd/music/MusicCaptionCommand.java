package xyz.hurrhnn.discordbot.cmd.music;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;
import xyz.hurrhnn.discordbot.util.SQL;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicCaptionCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(cmdContext.getGuild());

        if(musicManager.scheduler.isCaptionEnabled) {
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - caption", "Music captions are disabled.").build()).queue();
            musicManager.scheduler.isCaptionEnabled = false;
            return;
        }

        double restPing = Double.parseDouble(String.format("%.2f", (double)(cmdContext.getJDA().getRestPing().complete() / 1000)));
        if(musicManager.audioPlayer.getPlayingTrack() == null) {
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - caption", "```E: There are no songs in the queue.```").build()).queue();
            return;
        }

        try {
            Map<String, String> captionMap = new HashMap<>();
            YouTube youTube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),null).setApplicationName("JDA4 Discord Bot").build();
            List<SearchResult> results = youTube.search().list("id,snippet").setQ(musicManager.scheduler.player.getPlayingTrack().getInfo().uri).setMaxResults(1L).setType("video").setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)").setKey(SQL.getSQLData(SQL.initSQLConnection("discordjavabot"), "info", "token", cmdContext.getEvent())[2]).execute().getItems();

            String videoId = results.get(0).getId().getVideoId();
            String lc = cmdContext.getArgs().size() > 0 ? cmdContext.getArgs().get(0) : "ko";
            Connection.Response loginPageRequest = Jsoup.connect("http://video.google.com/timedtext?lang=" + lc + "&v=" + videoId)
                    .timeout(3000)
                    .header("Accept", "*/*")
                    .header("AJAX", "true")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Connection", "keep-alive")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:80.0) Gecko/20100101 Firefox/80.0")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            Document document = loginPageRequest.parse();

            for(Element element : document.getElementsByAttribute("start")) {
                captionMap.put(String.format("%.2f", Double.parseDouble(element.attr("start")) - restPing), element.text());
            }

            musicManager.scheduler.isCaptionEnabled = true;
            EmbedBuilder embedBuilder = EmbedUtils.embedMessageWithTitle("Music - caption", "```Please wait for caption..```");
            RestAction<Message> messageRestAction =  cmdContext.getChannel().sendMessage(embedBuilder.build());
            Message message = messageRestAction.complete();

            while ((musicManager.audioPlayer.getPlayingTrack() != null) && (musicManager.audioPlayer.getPlayingTrack().getState() == AudioTrackState.PLAYING) && (musicManager.scheduler.isCaptionEnabled)) {
                String currentTime = String.format("%.2f",((double) musicManager.scheduler.player.getPlayingTrack().getPosition() / 1000));
                if(captionMap.containsKey(currentTime)) {
                    if(!cmdContext.getChannel().getHistory().retrievePast(1).complete().get(0).getId().equals(message.getId())) {
                        message.delete().queue();
                        embedBuilder = EmbedUtils.embedMessageWithTitle("Music - caption", "```\n| " + StringEscapeUtils.unescapeHtml4(captionMap.get(currentTime)) + " |\n```");
                        messageRestAction = cmdContext.getChannel().sendMessage(embedBuilder.build());
                        message = messageRestAction.complete();
                    }
                    else cmdContext.getChannel().editMessageById(message.getId(), embedBuilder.setDescription("```\n| " + StringEscapeUtils.unescapeHtml4(captionMap.get(currentTime)) + " |\n```").build()).queue();
                    captionMap.remove(currentTime);
                } else if(captionMap.size() == 0) break;
                Thread.sleep(1);
            }
            cmdContext.getChannel().deleteMessageById(message.getId()).queue();
            musicManager.scheduler.isCaptionEnabled = false;
        } catch (Exception ignored) {
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - caption", "```E: No captions found on video.```").build()).queue();
            musicManager.scheduler.isCaptionEnabled = false;
        }
    }

    @Override
    public String getName() {
        return "caption";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("c");
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music caption [ISO Language Code]\n" +
                "-- Displays captions for music currently playing.\n" +
                "-- If a language code is defined, captions for that language are displayed.\n```";
    }
}
