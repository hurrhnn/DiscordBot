package xyz.hurrhnn.discordbot.cmd;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

public class PingCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        JDA jda= cmdContext.getJDA();

        jda.getRestPing().queue(
                (ping) -> cmdContext.getChannel()
                .sendMessageFormat("Pong! - `Reset Ping: %sms`, `WebSocket Ping: %sms`", ping, jda.getGatewayPing()).queue());
    }

    @Override
    public String getName() { return "ping"; }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!ping\n\n" +
                "-- Shows the current ping from the bot to the discord servers.\n```";
    }

    @Override
    public void errHandler(Exception e, TextChannel textChannel) { }
}
