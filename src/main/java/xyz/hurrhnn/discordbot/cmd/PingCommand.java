package xyz.hurrhnn.discordbot.cmd;

import net.dv8tion.jda.api.JDA;

public class PingCommand implements ICmd{
    @Override
    public void handle(CmdContext cmdContext) {
        JDA jda= cmdContext.getJDA();

        jda.getRestPing().queue(
                (ping) -> cmdContext.getChannel()
                .sendMessageFormat("Reset ping: %sms\nWebSocket ping: %sms", ping, jda.getGatewayPing()).queue());
    }

    @Override
    public String getName() { return "ping"; }

    @Override
    public String getHelp() {
        return "Shows the current ping from the bot to the discord servers.";
    }
}
