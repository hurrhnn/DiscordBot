package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class CleanChatCommand implements ICmd {

    @Override
    public void handle(CmdContext cmdContext) {

        TextChannel textChannel = cmdContext.getChannel();

        if(isArgsEmpty(cmdContext.getArgs())) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Wipe - Chat Cleaner", getHelp()).build()).queue();
            return;
        }

        try {
            int count = Integer.parseInt(cmdContext.getArgs().get(0));
            MessageHistory messageHistory = new MessageHistory(textChannel);
            List<Message> messages = messageHistory.retrievePast(count).complete();
            textChannel.deleteMessages(messages).complete();
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Wipe - Deleted " + count + (count == 1 ? " Message!" : " Messages!"), null).setFooter("Requested by " + cmdContext.getEvent().getAuthor().getAsTag(), cmdContext.getEvent().getAuthor().getAvatarUrl()).build()).queue();
        }catch (Exception e) { errHandler(e, cmdContext.getChannel()); }
    }

    @Override
    public String getName() {
        return "clean";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ !!clean [count]\n\n" +
                "-- Clean the chat neatly.\n" +
                "-- Only 2 to 100 lines can be cleaned. ```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("cc");
    }

    @Override
    public void errHandler(Exception e, TextChannel textChannel) {
        PrintStream errPrintStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try { errPrintStream = new PrintStream(byteArrayOutputStream, true, StandardCharsets.UTF_8.name()); } catch (UnsupportedEncodingException ignored) { }
        e.printStackTrace(errPrintStream);
        String error = byteArrayOutputStream.toString().split("\n")[0];

        if(error.contains("NumberFormatException") || error.contains("IndexOutOfBoundsException")) textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + "Error: Please enter a valid number." + "\n```").build()).queue();
        else if (error.contains("Message retrieval limit")) textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + "Error: Please enter a number from 2 to 99." + "\n```").build()).queue();
        else if (error.contains("PermissionException")) textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + "Error: The bot does NOT have the authority to delete the chat!" + "\n```").build()).queue();
        else textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + error + "\n```").build()).queue();
    }
}
