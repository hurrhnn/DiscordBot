package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;
import xyz.hurrhnn.discordbot.cmd.music.queue.MusicQueueCommandManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueCommand implements ICmd {

    final MusicQueueCommandManager manager = new MusicQueueCommandManager();

    @Override
    public void handle(CmdContext cmdContext) {
        ArrayList<String> argsList = new ArrayList<>(cmdContext.getArgs());
        argsList.add(0, "!!music");
        argsList.add(1, "queue");

        if (argsList.size() < 3) {
            StringBuilder stringBuilder = new StringBuilder();
            manager.getCommands().forEach(command -> stringBuilder.append(command.getHelp()).append("\n"));
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - Queue Usage", stringBuilder.toString()).build()).queue();
            return;
        }
        manager.handle(cmdContext.getEvent());
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music queue [command]\n" +
                "-- Manage the music player queue.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("q");
    }

    @Override
    public boolean isArgsEmpty(List<String> args) {
        return args.size() == 0;
    }
}
