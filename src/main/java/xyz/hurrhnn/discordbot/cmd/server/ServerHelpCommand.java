package xyz.hurrhnn.discordbot.cmd.server;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;
import xyz.hurrhnn.discordbot.util.Info;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ServerHelpCommand implements ICmd {

    private final ServerCommandManager manager;

    public ServerHelpCommand(ServerCommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CmdContext cmdContext) {
        List<String> args = cmdContext.getArgs();
        AtomicReference<TextChannel> textChannel = new AtomicReference<>(cmdContext.getChannel().asTextChannel());

        if(args.isEmpty())
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Available commands are: \n");

            manager.getCommands().stream().map(ICmd::getName).forEach((it) -> stringBuilder.append("`").append(Info.getPrefix(cmdContext.getEvent())).append("server ").append(it).append("`, "));
            textChannel.get().sendMessage(stringBuilder.substring(0, stringBuilder.length() - 2)).queue();
            return;
        }

        String search = args.get(0);
        ICmd cmd = manager.getCommand(search);

        if(cmd == null)
        {
            textChannel.get().sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```E: Nothing found for " + search).build()+ "```").queue();
            return;
        }
        textChannel.get().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Server - Usage", cmd.getHelp()).build()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!server help [command]\n" +
                "-- Shows the list with server commands in the bot.\n```";
    }
}
