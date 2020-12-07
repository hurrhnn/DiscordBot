package xyz.hurrhnn.discordbot.cmd.server;

import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;
import xyz.hurrhnn.discordbot.cmd.server.ServerCommandManager;

import java.util.Collections;
import java.util.List;

public class ServerCommand implements ICmd {

    final ServerCommandManager manager = new ServerCommandManager();

    @Override
    public void handle(CmdContext cmdContext) {

        if(isArgsEmpty(cmdContext.getArgs())) {
            StringBuilder stringBuilder = new StringBuilder();
            manager.getCommands().forEach(command -> stringBuilder.append(command.getHelp()).append("\n"));
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Server - Usage", stringBuilder.toString()).build()).queue();
            return;
        }
        manager.handle(cmdContext.getEvent());
    }

    @Override
    public String getName() {
        return "server";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!server [connect/disconnect/state] [address]\n" +
                "-- Shows server state.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("sv");
    }

    @Override
    public boolean isArgsEmpty(List<String> args) {
        return args.size() == 0;
    }
}
