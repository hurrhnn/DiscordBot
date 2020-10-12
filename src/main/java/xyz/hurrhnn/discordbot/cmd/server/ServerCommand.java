package xyz.hurrhnn.discordbot.cmd.server;

import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;
import xyz.hurrhnn.discordbot.cmd.server.ServerCommandManager;

import java.util.Collections;
import java.util.List;

public class ServerCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        final ServerCommandManager manager = new ServerCommandManager();
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
}
