package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import xyz.hurrhnn.discordbot.Main;
import xyz.hurrhnn.discordbot.util.SQL;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemeCommand implements ICmd {

    @Override
    public void handle(CmdContext cmdContext) {
        final TextChannel textChannel = cmdContext.getChannel().asTextChannel();
        String subReddit = "ProgrammerHumor";
        try {
            JSONObject childDataObject = retrieveAndParseJSON(subReddit);
            boolean isSavedURI = false;
            String[] memeLink = SQL.getSQLData(Main.con, "saved_meme", "meme_link", cmdContext.getEvent());
            String[] guildIndex = SQL.getSQLData(Main.con, "saved_meme", "guild_id", cmdContext.getEvent());
            for(int i = 0; i < guildIndex.length; i++)
            {
                if(memeLink[i].equals(childDataObject.get("url").toString()) && guildIndex[i].equals(cmdContext.getGuild().getId()))
                {
                    isSavedURI = true;
                    break;
                }
            }

            if (!childDataObject.get("url").toString().contains("https://i.redd.it/") || childDataObject.get("title").toString().length() <= 0 || childDataObject.get("permalink").toString().length() <= 0 || isSavedURI) childDataObject = reloadChildData(subReddit, cmdContext.getEvent());

            String title = childDataObject.get("title").toString();
            String url = "https://reddit.com" + childDataObject.get("permalink").toString();
            String image = childDataObject.get("url").toString();

            SQL.insertSQLData(Main.con, "saved_meme", (image + "\u200B" + cmdContext.getGuild().getName() + "\u200B" + cmdContext.getGuild().getId()).split("\u200B"), cmdContext.getEvent());

            EmbedBuilder embedBuilder = EmbedUtils.embedImageWithTitle(title, url, image);
            textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }catch (Exception e) { errHandler(e, cmdContext.getChannel().asTextChannel()); }
    }

    public JSONObject retrieveAndParseJSON(String subReddit) throws Exception {
        URL redditAPI = new URL("https://api.reddit.com/r/" + subReddit + "/random");
        HttpURLConnection con = (HttpURLConnection) redditAPI.openConnection();
        con.addRequestProperty("User-agent", "GiveMeMeMe");
        InputStream inputStream = con.getInputStream();
        Reader inputStreamReader = new InputStreamReader(inputStream);

        JSONParser jParser = new JSONParser();
        JSONArray rootArray = (JSONArray) jParser.parse(inputStreamReader);
        JSONObject jsonObject = (JSONObject) rootArray.get(0);
        JSONObject dataObject = (JSONObject) jsonObject.get("data");
        JSONArray childArray = (JSONArray) dataObject.get("children");
        JSONObject childObject = (JSONObject) childArray.get(0);

        return (JSONObject) childObject.get("data");
    }


    public JSONObject reloadChildData(String subReddit, MessageReceivedEvent event) {
        try {
            JSONObject childDataObject = retrieveAndParseJSON(subReddit);
            boolean isSavedURI = false;
            String[] memeLink = SQL.getSQLData(Main.con, "saved_meme", "meme_link", event);
            String[] guildIndex = SQL.getSQLData(Main.con, "saved_meme", "guild_id", event);

            for(int i = 0; i < guildIndex.length; i++)
            {
                if(memeLink[i].equals(childDataObject.get("url").toString()) && guildIndex[i].equals(event.getGuild().getId()))
                {
                    isSavedURI = true;
                    break;
                }
            }

            if (!childDataObject.get("url").toString().contains("https://i.redd.it/") || childDataObject.get("title").toString().length() <= 0 || childDataObject.get("permalink").toString().length() <= 0 || isSavedURI) return reloadChildData(subReddit, event);
            else return childDataObject;

        } catch (Exception ignored) { return reloadChildData(subReddit, event); }
    }

    @Override
    public String getName() {
        return "meme";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!meme\n" +
                "-- Show a random ProgrammingHumor from reddit.\n```";
    }

    @Override
    public void errHandler(Exception e, TextChannel textChannel)
    {
        StringBuilder errString = new StringBuilder();
        for(StackTraceElement stackTraceElement : e.getStackTrace()) { errString.append(stackTraceElement.toString()).append("\n"); }
        textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + errString.toString() + "\n```").build()).queue();
    }
}
