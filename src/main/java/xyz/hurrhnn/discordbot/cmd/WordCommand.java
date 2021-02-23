package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class WordCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        try {
            String arg = String.join("", cmdContext.getArgs()).toLowerCase().replaceAll("\\W", "");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL("https://suggest-bar.daum.net/suggest?mod=json&code=utf_in_out&enc=utf&id=language&cate=eng&q=" + arg).openConnection();

            InputStream inputStream = httpsURLConnection.getInputStream();
            Reader inputStreamReader = new InputStreamReader(inputStream);

            JSONParser jParser = new JSONParser();
            JSONObject rootObject = (JSONObject) jParser.parse(inputStreamReader);
            JSONArray jsonArray = new JSONArray(rootObject.get("items").toString());

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] data = jsonArray.getString(i).split("\\|");

                String originalEngData = data[1];
                if (cmdContext.getArgs().size() > 1) {
                    int spaceRemoveCnt = cmdContext.getArgs().size() - 1;
                    for (int j = 0; j < spaceRemoveCnt; j++)
                        data[1] = data[1].replaceFirst(" ", "");
                }

                if (data[1].contains(arg)) {
                    String[] filter = data[1].split(" ");
                    for (String string : filter) {
                        if (string.equals(arg)) {
                            stringBuilder.append(originalEngData).append(": ").append(data[2]).append("\n");
                            break;
                        }
                    }
                }
            }

            if (stringBuilder.toString().length() == 0)
                cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Word - Error!", "```Java\n" + "E: The word does not exist. Please enter the correct word." + "\n```").build()).queue();
            else
                cmdContext.getEvent().getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Word! - " + String.join(" ", cmdContext.getArgs()).toLowerCase().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", ""), "```\n" + stringBuilder.toString() + "\n```").build()).queue();
        } catch (Exception e) {
            errHandler(e, cmdContext.getChannel());
        }
    }

    @Override
    public void errHandler(Exception e, TextChannel textChannel) {
        PrintStream errPrintStream = null;
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        try {
            errPrintStream = new PrintStream(err, true, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        e.printStackTrace(errPrintStream);
        textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + err.toString().split("\n")[0] + "\n```").build()).queue();
    }

    @Override
    public String getName() {
        return "word";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!word [English Word]\n" +
                "-- Shows the related words and meanings of the English words you entered.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("w");
    }

    @Override
    public boolean isArgsEmpty(List<String> args) {
        return args.isEmpty();
    }
}
