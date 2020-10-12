package xyz.hurrhnn.discordbot.cmd.server;

import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class ConnectCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        try {
            ServerStateSocket.clientSocket = new Socket(InetAddress.getByName(cmdContext.getArgs().get(0)), 9090);
        }catch (IOException ignored)
        {
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Server - Connect", "```E: Can't connect remote server.").build()).queue();
        }
    }

    @Override
    public String getName() {
        return "connect";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!server connect [address]\n" +
                "-- Connect svState remote server.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("con");
    }
}
