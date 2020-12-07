package xyz.hurrhnn.discordbot.cmd.server;

import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class DisconnectCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        try {
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Server - Connect", "```Disconnected from " + ServerStateSocket.clientSocket.getRemoteSocketAddress().toString().substring(1) + " Server.```").build()).queue();
            ServerStateSocket.clientSocket.close();
        }catch (IOException ignored)
        {
            ServerStateSocket.clientSocket = null;
        }
    }

    @Override
    public String getName() {
        return "disconnect";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!server disconnect [address]\n" +
                "-- Disconnect svState remote server.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("dcon");
    }
}
