package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import xyz.hurrhnn.discordbot.Main;
import xyz.hurrhnn.discordbot.util.SQL;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MemeCommand implements ICmd {

    @Override
    public void handle(CmdContext cmdContext) {
        final TextChannel textChannel = cmdContext.getChannel();
        String subReddit = "ProgrammerHumor";

        try {
            URL redditAPI = new URL("https://api.reddit.com/r/" + subReddit + "/random");
            File tmpFile = new File(System.currentTimeMillis() + ".json");
            HttpURLConnection con = (HttpURLConnection) redditAPI.openConnection();
            con.addRequestProperty("User-agent", "GiveMeMeMe");
            InputStream inputStream = con.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, tmpFile);

            JSONParser jParser = new JSONParser();
            JSONArray rootArray = (JSONArray) jParser.parse(Files.readAllLines(Paths.get(tmpFile.getPath())).get(0));
            JSONObject jsonObject = (JSONObject) rootArray.get(0);
            JSONObject dataObject = (JSONObject) jsonObject.get("data");
            JSONArray childArray = (JSONArray) dataObject.get("children");
            JSONObject childObject = (JSONObject) childArray.get(0);
            JSONObject childDataObject = (JSONObject) childObject.get("data");

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
            textChannel.sendMessage(embedBuilder.build()).queue();

        } catch (Exception e) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.RED).setTitle("An error has occurred!").setDescription("```Java\n" + (e.getMessage().equals("https://api.reddit.com/r/" + subReddit + "/random") ? ("Does not exist subReddit \"" + subReddit + "\".") : e.getMessage()) + "\n```");
            textChannel.sendMessage(embedBuilder.build()).queue();
            e.printStackTrace();
        }
    }

    public static JSONObject reloadChildData(String subReddit, GuildMessageReceivedEvent event) {

        try {
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
            JSONObject childDataObject = (JSONObject) childObject.get("data");

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

        } catch (Exception ignored) {
            return reloadChildData(subReddit, event);
        }
    }

    @Override
    public String getName() {
        return "meme";
    }

    @Override
    public String getHelp() {
        return "Shows a Random ProgrammingHumor from reddit";
    }
}
