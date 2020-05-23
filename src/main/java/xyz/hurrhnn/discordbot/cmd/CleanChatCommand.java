package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.Collections;
import java.util.List;

public class CleanChatCommand implements ICmd{

    @Override
    public void handle(CmdContext cmdContext) {
        if (cmdContext.getArgs().size() != 1) return;

        TextChannel textChannel = cmdContext.getChannel();

        int count;
        try {
            count = Integer.parseInt(cmdContext.getArgs().get(0));
        } catch (Exception e) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\nError: \"It's not a number!\"```").build()).queue();
            return;
        }

        MessageHistory messageHistory = new MessageHistory(textChannel);
        List<Message> messages = messageHistory.retrievePast(count).complete();
        try {
            textChannel.deleteMessages(messages).complete();
        } catch (PermissionException e) {
            textChannel.sendMessage("채팅을 지울 수 있는 권한이 없어요!").queue();
            return;
        }
        textChannel.sendMessage(cmdContext.getEvent().getAuthor().getAsTag() + " 유저가 " + count + "개의 메세지를 삭제했습니다.").queue();
    }

    @Override
    public String getName() {
        return "cc";
    }

    @Override
    public String getHelp() {
        return "```diff\n-!!cc [count]\n\n" +
                "+ Clean the chat neatly.\n" +
                "+ Only 2 to 99 lines can be cleaned. ```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("clean");
    }
}
