package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.music.MusicCommandManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicCommand implements ICmd {

    final MusicCommandManager manager = new MusicCommandManager();

    @Override
    public void handle(CmdContext cmdContext) {
        ArrayList<String> argsList = new ArrayList<>(cmdContext.getArgs());
        argsList.add(0, "!!music");

        if (argsList.size() < 2) {
            cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Usage", getHelp()).build()).queue();
            return;
        }
        manager.handle(cmdContext.getEvent());
    }

    @Override
    public String getName() {
        return "music";
    }

    @Override
    public String getHelp() {
        StringBuilder stringBuilder = new StringBuilder();
        manager.getCommands().forEach(command -> stringBuilder.append(command.getHelp()).append("\n"));
        return stringBuilder.toString();
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("m");
    }
}
